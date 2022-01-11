/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

public class TableHeader implements Serializable {

    private final String text;
    private final int colspan;
    private final String title;
    private final String meta;

    public TableHeader(String text, int colspan, String title, String meta) {
        this.text = text;
        this.colspan = colspan;
        this.title = title;
        this.meta = meta;
    }

    public TableHeader(String text, int colspan) {
        this(text, colspan, null, null);
    }

    public TableHeader(String text) {
        this(text, 1);
    }

    public String getText() {
        return text;
    }

    public int getColspan() {
        return colspan;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public String getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return text + ";" + colspan;
    }

}
