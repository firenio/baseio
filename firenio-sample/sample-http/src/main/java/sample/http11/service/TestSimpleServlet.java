/*
 * Copyright 2015 The FireNio Project
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
package sample.http11.service;

import org.springframework.stereotype.Service;

import com.firenio.codec.http11.HttpFrame;
import com.firenio.component.Channel;

import sample.http11.HttpFrameAcceptor;

@Service("/test")
public class TestSimpleServlet extends HttpFrameAcceptor {

    @Override
    protected void doAccept(Channel ch, HttpFrame f) throws Exception {
        String res = "yes server already accept your message :) " + f.getRequestParams();
        f.setString(res, ch);
        ch.writeAndFlush(f);
    }
}
