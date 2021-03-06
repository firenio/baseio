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
package sample.http11;

import com.firenio.codec.http11.HttpFrame;
import com.firenio.component.Frame;
import com.firenio.component.Channel;

/**
 * @author wangkai
 */
public abstract class HttpFrameAcceptor {

    public void accept(Channel ch, Frame frame) throws Exception {
        doAccept(ch, (HttpFrame) frame);
    }

    protected void doAccept(Channel ch, HttpFrame frame) throws Exception {

    }

}
