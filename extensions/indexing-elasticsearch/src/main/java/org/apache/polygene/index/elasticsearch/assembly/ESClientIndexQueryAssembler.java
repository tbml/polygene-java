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
 */
package org.apache.polygene.index.elasticsearch.assembly;

import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.index.elasticsearch.ElasticSearchIndexingConfiguration;
import org.apache.polygene.index.elasticsearch.client.ESClientIndexQueryService;
import org.apache.polygene.index.elasticsearch.internal.AbstractElasticSearchAssembler;
import org.elasticsearch.client.Client;

public class ESClientIndexQueryAssembler
    extends AbstractElasticSearchAssembler<ESClientIndexQueryAssembler>
{
    private final Client client;

    public ESClientIndexQueryAssembler( final Client client )
    {
        this.client = client;
    }

    @Override
    public void assemble( final ModuleAssembly module )
    {
        super.assemble( module );
        module.services( ESClientIndexQueryService.class )
              .taggedWith( "elasticsearch", "query", "indexing" )
              .identifiedBy( identity() )
              .setMetaInfo( client )
              .visibleIn( visibility() )
              .instantiateOnStartup();

        if( hasConfig() )
        {
            configModule().entities( ElasticSearchIndexingConfiguration.class )
                          .visibleIn( configVisibility() );
        }
    }
}
