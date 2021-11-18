/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2021 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package EOorg.EOeolang.EOgray;

import java.util.concurrent.atomic.AtomicReference;
import org.eolang.AtComposite;
import org.eolang.AtFree;
import org.eolang.Attr;
import org.eolang.Data;
import org.eolang.PhDefault;
import org.eolang.Phi;
import org.eolang.XmirObject;

/**
 * CAGE.
 *
 * @since 1.0
 */
@XmirObject(oname = "memory")
public class EOcage extends PhDefault {

    private final AtomicReference<Phi> phi;

    public EOcage(final Phi sigma) {
        super(sigma);
        this.phi = new AtomicReference<>();
        this.add("φ", new AtComposite(this, rho -> {
            final Phi object = this.phi.get();
            if (object == null) {
                throw new Attr.IllegalAttrException(
                    "The cage is still empty"
                );
            }
            return object.copy(rho);
        }));
        this.add("write", new AtComposite(this, EOcage.Write::new));
        this.add("is-empty", new AtComposite(this, EOcage.IsEmpty::new));
    }

    private final class Write extends PhDefault {
        Write(final Phi sigma) {
            super(sigma);
            this.add("x", new AtFree());
            this.add("φ", new AtComposite(this, rho -> {
                final Phi obj = rho.attr("x").get();
                EOcage.this.phi.set(obj);
                return new Data.ToPhi(true);
            }));
        }
    }

    private final class IsEmpty extends PhDefault {
        IsEmpty(final Phi sigma) {
            super(sigma);
            this.add("φ", new AtComposite(
                this, rho -> new Data.ToPhi(EOcage.this.phi.get() == null)
            ));
        }
    }

}
