/*
 * Copyright 2010-2011 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;
import net.sf.oval.constraint.MinLength;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

import javax.persistence.*;

import controllers.CRUD.Exclude;

import java.util.Arrays;
import java.util.List;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Entity
@Table(name = "bnbm_user")
public class User extends Model implements RoleHolder
{
    @Required
    @Unique
    public String username;

    @Exclude
    public String name;
    
    @Password
    @Required
    @MinSize(6)
    public String password;

    @Required
    @ManyToOne
    public ApplicationRole role;

    @Exclude
    @OneToOne
    public Profile profile;

    @ManyToMany
    public List<Material> materials;

    public User(String userName,
                String password,
                ApplicationRole role)
    {
        this.username = userName;
        this.password = password;
        this.name = userName;
        this.role = role;
    }

    public static User getByUserName(String userName)
    {
        return find("byUserName", userName).first();
    }

    @Override
    public String toString()
    {
        return this.username;
    }


    public List<? extends Role> getRoles()
    {
        return Arrays.asList(role);
    }
    
    public static User connect(String username, String password) {
        return find("byUsernameAndPassword", username, password).first();
    }

}
