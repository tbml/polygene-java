/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.api.property;

/**
 * Properties are declared in Composite interfaces
 * by using this interface. It creates a first-class
 * object for the property from which you can get and
 * set the value, and access any metadata about it.
 * The generic type of the Property must be fully
 * Serializable and must not have any injected members.
 */
public interface Property<T>
    extends PropertyInfo
{
    /**
     * Get the value of the property.
     *
     * @return the value
     */
    T get();

    /**
     * Set the value of the property
     *
     * @param newValue the new value
     * @throws IllegalArgumentException if the value has an invalid value
     * @throws IllegalStateException    if the property is immutable or computed
     */
    void set( T newValue )
        throws IllegalArgumentException, IllegalStateException;
}