/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.apache.polygene.runtime.injection.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.polygene.api.service.NoSuchServiceTypeException;
import org.apache.polygene.api.service.ServiceReference;
import org.apache.polygene.api.service.qualifier.Qualifier;
import org.apache.polygene.api.util.Classes;
import org.apache.polygene.bootstrap.InvalidInjectionException;
import org.apache.polygene.runtime.injection.DependencyModel;
import org.apache.polygene.runtime.injection.InjectionContext;
import org.apache.polygene.runtime.injection.InjectionProvider;
import org.apache.polygene.runtime.injection.InjectionProviderFactory;
import org.apache.polygene.runtime.model.Resolution;

import static java.util.stream.Collectors.toCollection;
import static org.apache.polygene.api.util.Annotations.typeHasAnnotation;

public final class ServiceInjectionProviderFactory
    implements InjectionProviderFactory
{
    @Override
    @SuppressWarnings( "unchecked" )
    public InjectionProvider newInjectionProvider( Resolution resolution, DependencyModel dependencyModel )
        throws InvalidInjectionException
    {
        // TODO This could be changed to allow multiple @Qualifier annotations
        Annotation qualifierAnnotation = Stream.of( dependencyModel.annotations() )
                                               .filter( typeHasAnnotation( Qualifier.class ) )
                                               .findFirst().orElse( null );
        Predicate<ServiceReference<?>> serviceQualifier = null;
        if( qualifierAnnotation != null )
        {
            Qualifier qualifier = qualifierAnnotation.annotationType().getAnnotation( Qualifier.class );
            try
            {
                serviceQualifier = qualifier.value().newInstance().qualifier( qualifierAnnotation );
            }
            catch( Exception e )
            {
                throw new InvalidInjectionException( "Could not instantiate qualifier serviceQualifier", e );
            }
        }

        if( dependencyModel.rawInjectionType().equals( Iterable.class ) )
        {
            Type iterableType = ( (ParameterizedType) dependencyModel.injectionType() ).getActualTypeArguments()[ 0 ];
            if( Classes.RAW_CLASS.apply( iterableType ).equals( ServiceReference.class ) )
            {
                // @Service Iterable<ServiceReference<MyService<Foo>> serviceRefs
                Type serviceType = ( (ParameterizedType) iterableType ).getActualTypeArguments()[ 0 ];

                return new IterableServiceReferenceProvider( serviceType, serviceQualifier );
            }
            else
            {
                // @Service Iterable<MyService<Foo>> services
                return new IterableServiceProvider( iterableType, serviceQualifier );
            }
        }
        else if( dependencyModel.rawInjectionType().equals( ServiceReference.class ) )
        {
            // @Service ServiceReference<MyService<Foo>> serviceRef
            Type referencedType = ( (ParameterizedType) dependencyModel.injectionType() ).getActualTypeArguments()[ 0 ];
            return new ServiceReferenceProvider( referencedType, serviceQualifier );
        }
        else
        {
            // @Service MyService<Foo> service
            return new ServiceProvider( dependencyModel.injectionType(), serviceQualifier );
        }
    }

    private static class IterableServiceReferenceProvider
        extends ServiceInjectionProvider
    {
        private IterableServiceReferenceProvider( Type serviceType, Predicate<ServiceReference<?>> serviceQualifier )
        {
            super( serviceType, serviceQualifier );
        }

        @Override
        public synchronized Object provideInjection( InjectionContext context )
            throws InjectionProviderException
        {
            return getServiceReferences( context ).collect( toCollection( ArrayList::new ) );
        }
    }

    private static class IterableServiceProvider
        extends ServiceInjectionProvider
        implements Function<ServiceReference<?>, Object>
    {
        private IterableServiceProvider( Type serviceType, Predicate<ServiceReference<?>> serviceQualifier )
        {
            super( serviceType, serviceQualifier );
        }

        @Override
        public synchronized Object provideInjection( final InjectionContext context )
            throws InjectionProviderException
        {
            return getServiceReferences( context ).map( ServiceReference::get )
                                                  .collect( toCollection( ArrayList::new ) );
        }

        @Override
        public Object apply( ServiceReference<?> objectServiceReference )
        {
            return objectServiceReference.get();
        }
    }

    private static class ServiceReferenceProvider
        extends ServiceInjectionProvider
    {
        ServiceReferenceProvider( Type serviceType, Predicate<ServiceReference<?>> qualifier )
        {
            super( serviceType, qualifier );
        }

        @Override
        public synchronized Object provideInjection( InjectionContext context )
            throws InjectionProviderException
        {
            return getServiceReference( context );
        }
    }

    private static class ServiceProvider
        extends ServiceInjectionProvider
    {
        ServiceProvider( Type serviceType, Predicate<ServiceReference<?>> qualifier )
        {
            super( serviceType, qualifier );
        }

        @Override
        public synchronized Object provideInjection( InjectionContext context )
            throws InjectionProviderException
        {
            ServiceReference<?> ref = getServiceReference( context );

            if( ref != null )
            {
                return ref.get();
            }
            else
            {
                return null;
            }
        }
    }

    public abstract static class ServiceInjectionProvider
        implements InjectionProvider
    {
        private final Type serviceType;
        private final Predicate<ServiceReference<?>> serviceQualifier;

        private ServiceInjectionProvider( Type serviceType, Predicate<ServiceReference<?>> serviceQualifier )
        {
            this.serviceType = serviceType;
            this.serviceQualifier = serviceQualifier;
        }

        protected ServiceReference<Object> getServiceReference( InjectionContext context )
        {
            try
            {
                if( serviceQualifier == null )
                {
                    return context.module().instance().findService( serviceType );
                }
                else
                {
                    return context.module().instance().findServices( serviceType )
                                  .filter( serviceQualifier ).findFirst().orElse( null );
                }
            }
            catch( NoSuchServiceTypeException e )
            {
                return null;
            }
        }

        protected Stream<ServiceReference<Object>> getServiceReferences( final InjectionContext context )
        {
            if( serviceQualifier == null )
            {
                return context.module().instance().findServices( serviceType );
            }
            else
            {
                return context.module().instance().findServices( serviceType ).filter( serviceQualifier );
            }
        }
    }
}
