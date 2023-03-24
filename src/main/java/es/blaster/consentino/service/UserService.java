/*
 * Copyright (c) 2023
 *
 * by Rafael Angel Aznar Aparici (rafaaznar at gmail dot com) & DAW students
 *
 * CONSENTINO: JWT GENERATOR MICROSERVICE
 *
 * Sources at:                https://github.com/rafaelaznar/consentino
 * Database at:               https://github.com/rafaelaznar/consentino
 * POSTMAN API at:            https://github.com/rafaelaznar/consentino
 * Frontend at:               https://github.com/rafaelaznar/accord
 *
 * CONSENTINO is distributed under the MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package es.blaster.consentino.service;


import es.blaster.consentino.repository.UsertypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import es.blaster.consentino.entity.UserEntity;
import es.blaster.consentino.exception.ResourceNotFoundException;
import es.blaster.consentino.exception.ResourceNotModifiedException;
import es.blaster.consentino.exception.ValidationException;
import es.blaster.consentino.helper.ValidationHelper;
import es.blaster.consentino.repository.UserRepository;

@Service
public class UserService {
    
    private final UsertypeService oUsertypeService;
    private final UserRepository oDeveloperRepository;
    private final UsertypeRepository oUsertypeRepository;
    private final AuthService oAuthService;    

    @Autowired
    public UserService(UserRepository oUserRepository, UsertypeRepository oUsertypeRepository, UsertypeService oUsertypeService, AuthService oAuthService) {
        this.oUsertypeService=oUsertypeService;
        this.oDeveloperRepository = oUserRepository;
        this.oUsertypeRepository = oUsertypeRepository;
        this.oAuthService = oAuthService;
    }

    public void validate(Long id) {
        if (!oDeveloperRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exists");
        }
    }

    public void validate(UserEntity oDeveloperEntity) {
        ValidationHelper.validateStringLength(oDeveloperEntity.getName(), 2, 50, "user name (between 2-50 chars)");
        ValidationHelper.validateStringLength(oDeveloperEntity.getSurname(), 2, 50, "surname (between 2-50 chars)");
        ValidationHelper.validateStringLength(oDeveloperEntity.getLastname(), 2, 50, "lastname (between 2-50 chars)");
        ValidationHelper.validateEmail(oDeveloperEntity.getEmail(), "user email");
        ValidationHelper.validateLogin(oDeveloperEntity.getUsername(), "username");
        ValidationHelper.validateStringLength(oDeveloperEntity.getPassword(),64,64, "password must be SHA256 encripted");
        if (oDeveloperRepository.existsByUsername(oDeveloperEntity.getUsername())) {
            throw new ValidationException("username repeated");
        }
        oUsertypeService.validate(oDeveloperEntity.getUsertype().getId());
    }

    public UserEntity get(Long id) {
        oAuthService.OnlyAdmins();
        return oDeveloperRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("user with id: " + id + " not found"));
    }

    public Page<UserEntity> getPage(Pageable oPageable, String strFilter, Long id_usertype) {
        oAuthService.OnlyAdmins();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        if (strFilter == null || strFilter.length() == 0) {
            if (id_usertype == null) {
                return oDeveloperRepository.findAll(oPageable);
            } else {
                return oDeveloperRepository.findByUsertypeId(id_usertype, oPageable);
            }
        } else {
            if (id_usertype == null) {
                return oDeveloperRepository.findByNameIgnoreCaseContainingOrSurnameIgnoreCaseContainingOrLastnameIgnoreCaseContaining(strFilter, strFilter, strFilter, oPageable);
            } else {
                return oDeveloperRepository.findByNameIgnoreCaseContainingOrSurnameIgnoreCaseContainingOrLastnameIgnoreCaseContainingAndUsertypeId(strFilter, strFilter, strFilter, id_usertype, oPageable);
            }
        }

    }

    public Long count() {
        oAuthService.OnlyAdmins();
        return oDeveloperRepository.count();
    }

    public Long create(UserEntity oNewDeveloperEntity) {
        oAuthService.OnlyAdmins();
        validate(oNewDeveloperEntity);
        oNewDeveloperEntity.setId(0L);        
        return oDeveloperRepository.save(oNewDeveloperEntity).getId();
    }

    public Long update(UserEntity oDeveloperEntity) {
        validate(oDeveloperEntity.getId());
        oAuthService.OnlyAdmins();
        UserEntity oOldDeveloperEntity = oDeveloperRepository.getById(oDeveloperEntity.getId());        
        return oDeveloperRepository.save(oDeveloperEntity).getId();
    }

    public Long delete(Long id) {
        oAuthService.OnlyAdmins();
        validate(id);
        oDeveloperRepository.deleteById(id);
        if (oDeveloperRepository.existsById(id)) {
            throw new ResourceNotModifiedException("can't remove user register with id=" + id);
        } else {
            return id;
        }
    }


}
