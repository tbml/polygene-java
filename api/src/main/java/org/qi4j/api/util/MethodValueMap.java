/*
 * Copyright 2009 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.api.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a HashMap subtype where the Value is a Method, for the pure purpose of being Serializable.
 *
 * @param <K> The Key in the HashMap.
 */
public class MethodValueMap<K> extends HashMap<K, Method>
    implements Externalizable
{
    static final long serialVersionUID = 1L;

    public void writeExternal( ObjectOutput out )
        throws IOException
    {
        out.writeInt( size() );
        for( Map.Entry<K, Method> entry : entrySet() )
        {
            out.writeObject( entry.getKey() );
            Method m = entry.getValue();
            SerializationUtil.writeMethod( out, m );
        }
    }

    public void readExternal( ObjectInput in )
        throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        for( int i = 0; i < size; i++ )
        {
            K key = (K) in.readObject();
            Method method = SerializationUtil.readMethod( in );
            put( key, method );
        }
    }
}