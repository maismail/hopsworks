/*
 * This file is part of Hopsworks
 * Copyright (C) 2019, Logical Clocks AB. All rights reserved
 *
 * Hopsworks is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Hopsworks is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package io.hops.hopsworks.admin.user.administration;

import io.hops.hopsworks.admin.maintenance.MessagesController;
import io.hops.hopsworks.common.dao.user.UserFacade;
import io.hops.hopsworks.persistence.entity.user.Users;
import io.hops.hopsworks.persistence.entity.user.security.ua.UserAccountStatus;
import io.hops.hopsworks.persistence.entity.user.security.ua.UserAccountType;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class UsersAdministrationBean implements Serializable {
  
  private static final Logger LOGGER = Logger.getLogger(UsersAdministrationBean.class.getName());
  
  @EJB
  private UserFacade userFacade;
  @EJB
  protected UserAdministrationController userAdministrationController;
  
  private LazyDataModel<Users> lazyUsers;
  
  @PostConstruct
  public void init() {
    lazyUsers = new UsersAdminLazyDataModel(userFacade);
  }
  
  public LazyDataModel<Users> getLazyUsers() {
    return lazyUsers;
  }
  
  public UserAccountStatus[] getAccountStatuses() {
    return UserAccountStatus.values();
  }
  
  public UserAccountType[] getAccountTypes() {
    return UserAccountType.values();
  }
  
  public String getAccountType(UserAccountType type) {
    return userAdministrationController.getAccountType(type);
  }
  
  public String getUserStatus(UserAccountStatus status) {
    return userAdministrationController.getUserStatus(status);
  }
  
  public String modifyUser(Users user) {
    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("editinguser", user);
    MessagesController.addInfoMessage("User successfully modified for " + user.getEmail());
    return "admin_profile";
  }
}
