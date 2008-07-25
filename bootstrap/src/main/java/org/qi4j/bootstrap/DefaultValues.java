/*
 * Copyright (c) 2008, Michael Hunger. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.bootstrap;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class DefaultValues
{
    private static final Map<Type, Object> defaultValues = createDefaultValues();

    private static Map<Type, Object> createDefaultValues()
    {
        Map<Type, Object> defaultValues = new HashMap<Type, Object>();
        defaultValues.put( Byte.class, 0 );
        defaultValues.put( Short.class, 0 );
        defaultValues.put( Character.class, 0 );
        defaultValues.put( Integer.class, 0 );
        defaultValues.put( Long.class, 0L );
        defaultValues.put( Double.class, 0D );
        defaultValues.put( Float.class, 0F );
        defaultValues.put( Boolean.class, false );
        return defaultValues;
    }

    public static Object getDefaultValue( Type type )
    {
        return defaultValues.get( type );
    }
}