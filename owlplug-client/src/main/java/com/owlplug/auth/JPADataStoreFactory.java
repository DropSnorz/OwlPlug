/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.auth;

import com.google.api.client.util.store.DataStoreFactory;
import com.owlplug.auth.dao.GoogleCredentialDAO;
import java.io.IOException;

/**
 * JPADataStore factory. Creates new {@link JPADataStore} instances.
 *
 */
public class JPADataStoreFactory implements DataStoreFactory {

  private GoogleCredentialDAO repository;

  public JPADataStoreFactory(GoogleCredentialDAO repository) {
    this.repository = repository;
  }

  @Override
  public JPADataStore getDataStore(String id) throws IOException {
    return new JPADataStore(this, id, repository);
  }

}
