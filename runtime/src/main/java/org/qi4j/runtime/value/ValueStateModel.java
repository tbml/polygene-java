/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.runtime.value;

import org.qi4j.api.property.StateHolder;
import org.qi4j.runtime.composite.AbstractStateModel;
import org.qi4j.runtime.property.PropertiesInstance;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.spi.value.ValueState;

/**
 * State model for values
 */
public final class ValueStateModel
    extends AbstractStateModel<ValuePropertiesModel>
{
    public ValueStateModel( ValuePropertiesModel propertiesModel )
    {
        super( propertiesModel );
    }

    public StateHolder newInstance( ModuleInstance moduleInstance, ValueState state )
    {
        PropertiesInstance properties = propertiesModel.newInstance( moduleInstance, state );
        return new StateInstance( properties );
    }
}