/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007, Niclas Hedhman. All Rights Reserved.
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
package iop.api;

import iop.api.persistence.Identity;
import iop.api.persistence.ObjectNotFoundException;
import iop.api.persistence.binding.PersistenceBinding;

/**
 * This repository is used to get proxies representing persistent objects. 
 *
 */
public interface ObjectRepository
{
   /**
    * Get a proxy to a persistent object. This proxy may have been cached
    * and returned from previous invocations with the same identity.
    *
    * @param anIdentity
    * @param aType
    * @return
    */
   <T extends PersistenceBinding> T getInstance(String anIdentity, Class<T> aType);
}
