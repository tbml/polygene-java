/*  Copyright 2007 Niclas Hedhman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.orthogon.samples.common.mixin;

import org.ops4j.orthogon.mixin.QiMixin;

@QiMixin
public interface Address
{
    String getCity();

    String getCountry();

    String getStreetAddress();

    String getZipCode();

    void setCity( String city );

    void setCountry( String country );

    void setStreetAddress( String street );

    void setZipCode( String zipcode );
}
