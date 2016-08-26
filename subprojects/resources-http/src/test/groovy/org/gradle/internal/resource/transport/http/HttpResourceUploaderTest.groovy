/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.resource.transport.http

import org.apache.http.HttpEntity
import org.apache.http.ProtocolVersion
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.message.BasicStatusLine
import org.gradle.internal.resource.local.LocalResource
import spock.lang.Specification

class HttpResourceUploaderTest extends Specification {

    def 'uploader closes the request'() {
        given:
        HttpClientHelper client = Mock()
        LocalResource resource = Mock()
        CloseableHttpResponse response = Mock()
        HttpEntity entity = Mock()
        InputStream content = Mock()

        when:
        new HttpResourceUploader(client).upload(resource, new URI("http://somewhere.org/somehow"))

        then:
        1 * client.performHttpRequest(_) >> response
        _ * response.getStatusLine() >> new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 400, "I'm broken")
        1 * response.close()
        1 * response.getEntity() >> entity
        1 * entity.isStreaming() >> true
        1 * entity.content >> content
        1 * content.close()

        IOException exception = thrown()
        exception.message.contains('Could not PUT')
    }
}