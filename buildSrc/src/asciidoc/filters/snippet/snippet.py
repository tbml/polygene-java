#!/usr/bin/env python
# -*- mode: Python; coding: utf-8 -*-
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""
source=ignored
tag=self-test
"""

import sys

PATH_PATTERN="%(source)s"

def configuration(indata):
    config = {}
    for line in indata:
        line = line.strip()
        if not line: continue
        try:
            key, value = line.split('=',1)
        except:
            raise ValueError('invalid config line: "%s"' % (line,))
        config[key] = value
    return config

def snippet(source=None, tag=None, tablength="4", snipMarker="  [...snip...]\n\n", **other):
    for key in other:
        sys.stderr.write("WARNING: unknown config key: '%s'\n" % key)
    if not tag: raise ValueError("'tag' must be specified")
    if not source: raise ValueError("'source' must be specified")
    try:
        tablength = ' ' * int(tablength)
    except:
        raise ValueError("'tablength' must be specified as an integer")

    START = "START SNIPPET: %s" % tag
    END = "END SNIPPET: %s" % tag

    sourceFile = open(PATH_PATTERN % locals())

    try:
        # START SNIPPET: self-test
        buff = []
        mindent = 1<<32 # a large number - no indentation is this long
        emit = False
        emitted = False

        for line in sourceFile:
            if line.strip().endswith(END): emit = False
            if emit:
                emitted = True
                if not "SNIPPET" in line:
                    line = line.replace(']]>',']]>]]&gt;<![CDATA[')
                    meat = line.lstrip()
                    if meat:
                        indent = line[:-len(meat)].replace('\t', tablength)
                        mindent = min(mindent, len(indent))
                        buff.append(indent + meat)
                    else:
                        buff.append('')
            if line.strip().endswith(START):
                if emitted:
                    buff.append(indent + snipMarker)
                emit = True
        # END SNIPPET: self-test

    finally:
        sourceFile.close()

    if not buff:
        raise ValueError('Missing snippet for tag "' + tag + '" in file "' + source + '".')
    for line in buff:
        if line:
            yield line[mindent:]
        else:
            yield '\n'


if __name__ == '__main__':
    import traceback
    indata = sys.stdin
    if len(sys.argv) == 2 and sys.argv[1] == '--self-test':
        PATH_PATTERN = __file__
        indata = __doc__.split('\n')
    try:
        # START SNIPPET: self-test
        sys.stdout.write("<![CDATA[")
        for line in snippet(**configuration(indata)):
            sys.stdout.write(line)
        # END SNIPPET: self-test
    except:
        traceback.print_exc(file=sys.stdout)
        raise
    finally:
        # START SNIPPET: self-test
        sys.stdout.write("]]>")
        # END SNIPPET: self-test
