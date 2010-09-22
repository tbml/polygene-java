/*
 * Copyright (c) 2010, Stanislav Muhametsin. All Rights Reserved.
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

package org.qi4j.index.sql.internal;

import java.sql.Connection;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.service.Activatable;
import org.qi4j.library.sql.api.SQLAppStartup;

/**
 * @author Stanislav Muhametsin
 */
public class SQLActivatable implements Activatable
{
    @Service
    private SQLAppStartup _startup;

    @This
    private SQLJDBCState _state;

    public void activate()
        throws Exception
    {
        Connection connection = this._startup.createConnection();
        this._state.connection().set( connection );
        this._startup.initConnection( connection );
    }

    public void passivate()
        throws Exception
    {
        // Nothing to do.
    }
}
