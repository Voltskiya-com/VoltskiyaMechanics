package com.voltskiya.mechanics.database;

import io.ebean.Model;
import io.ebean.annotation.DbName;
import javax.persistence.MappedSuperclass;

@DbName(MechanicsDatabase.NAME)
@MappedSuperclass
public class BaseEntity extends Model {

    public BaseEntity() {
        super(MechanicsDatabase.NAME);
    }
}
