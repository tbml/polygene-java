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
package org.apache.polygene.entitystore.jdbm;

import org.apache.polygene.api.common.Optional;
import org.apache.polygene.api.common.UseDefaults;
import org.apache.polygene.api.property.Property;

/**
 * Configuration for the JdbmEntityStoreService.
 */
// START SNIPPET: config
public interface JdbmEntityStoreConfiguration
{
    // END SNIPPET: config
   /**
    * The file where the JDBM data will be stored
    * <p>
    * Default: System.getProperty( "user.dir" ) + "/polygene/jdbmstore.data";
    * </p>
    * @return path to data file relative to current path
    */
    // START SNIPPET: config
   @Optional
   Property<String> file();

   // JDBM RecordManager options

   @UseDefaults
   Property<Boolean> autoCommit();

   @UseDefaults
   Property<Boolean> disableTransactions();
}
// END SNIPPET: config
