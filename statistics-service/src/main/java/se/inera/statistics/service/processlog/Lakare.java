/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.processlog;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Lakare {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String vardgivareId;

    private String lakareId;

    private String tilltalsNamn;

    private String efterNamn;

    public Lakare(String vardgivareId, String lakareId, String tilltalsNamn, String efterNamn) {
        this.vardgivareId = vardgivareId;
        this.lakareId = lakareId;
        this.tilltalsNamn = tilltalsNamn;
        this.efterNamn = efterNamn;
    }

    public Lakare() {}

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getVardgivareId() {
        return vardgivareId;
    }

    public void setVardgivareId(String vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public String getLakareId() {
        return lakareId;
    }

    public void setLakareId(String lakareId) {
        this.lakareId = lakareId;
    }

    public String getTilltalsNamn() {
        return tilltalsNamn;
    }

    public void setTilltalsNamn(String tilltalsNamn) {
        this.tilltalsNamn = tilltalsNamn;
    }

    public String getEfterNamn() {
        return efterNamn;
    }

    public void setEfterNamn(String efterNamn) {
        this.efterNamn = efterNamn;
    }

}
