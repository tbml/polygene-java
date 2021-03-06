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

package org.apache.polygene.library.spring.importer;

import org.apache.polygene.api.common.Visibility;
import org.apache.polygene.bootstrap.Assembler;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.springframework.context.ApplicationContext;

/**
 * Imports all services in the given Spring ApplicationContext into a module.
 */
public class SpringImporterAssembler
    implements Assembler
{
    private ApplicationContext context;
    private Visibility defaultVisibility;

    /**
     * Import all beans from the given ApplicationContext as services in Polygene,
     * using Module as Visibility.
     *
     * @param context the Spring ApplicationContext
     */
    public SpringImporterAssembler( ApplicationContext context )
    {
        this( context, Visibility.module );
    }


    /**
     * Import all beans from the given ApplicationContext as services in Polygene,
     * using the specified Visibility level.
     *
     * @param context           the Spring ApplicationContext
     * @param defaultVisibility the visibility level for the imported services
     */
    public SpringImporterAssembler( ApplicationContext context, Visibility defaultVisibility )
    {
        this.context = context;
        this.defaultVisibility = defaultVisibility;
    }

    @Override
    public void assemble( ModuleAssembly module )
    {
        // Register all Spring beans as services
        String[] names = context.getBeanDefinitionNames();
        for( String name : names )
        {
            Class serviceType = context.getType( name );
            module.
                importedServices( serviceType ).
                importedBy( SpringImporter.class ).
                identifiedBy( name ).
                setMetaInfo( context ).
                visibleIn( defaultVisibility );
        }
    }
}
