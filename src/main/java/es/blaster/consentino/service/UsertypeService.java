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

import es.blaster.consentino.entity.UsertypeEntity;
import es.blaster.consentino.exception.ResourceNotFoundException;
import es.blaster.consentino.exception.ResourceNotModifiedException;
import es.blaster.consentino.exception.ValidationException;
import es.blaster.consentino.helper.ValidationHelper;
import es.blaster.consentino.repository.UsertypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsertypeService {

    private final UsertypeRepository oUsertypeRepository;
    private final AuthService oAuthService;

    @Autowired
    public UsertypeService(UsertypeRepository oUsertypeRepository, AuthService oAuthService) {
        this.oUsertypeRepository = oUsertypeRepository;
        this.oAuthService = oAuthService;
    }

    public void validate(Long id) {
        if (!oUsertypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public void validate(UsertypeEntity oUsertypeEntity) {
        ValidationHelper.validateStringLength(oUsertypeEntity.getName(), 2, 50, "usertype name (between 2 to 50 chars)");
        if (oUsertypeRepository.existsByName(oUsertypeEntity.getName())) {
            throw new ValidationException("usertype repeated");
        }
    }

    public UsertypeEntity get(Long id) {
        oAuthService.OnlyAdmins();
        return oUsertypeRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("UserType with id: " + id + " not found"));
    }

    public Long count() {
        oAuthService.OnlyAdmins();
        return oUsertypeRepository.count();
    }

    public Page<UsertypeEntity> getPage(Pageable oPageable, String strFilter) {
        oAuthService.OnlyAdmins();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        if (strFilter == null || strFilter.length() == 0) {
            return oUsertypeRepository.findAll(oPageable);
        } else {
            return oUsertypeRepository.findByNameIgnoreCaseContaining(strFilter, oPageable);
        }
    }

    public Long create(UsertypeEntity oUsertypeEntity) {
        oAuthService.OnlyAdmins();
        validate(oUsertypeEntity);
        oUsertypeEntity.setId(0L);
        return oUsertypeRepository.save(oUsertypeEntity).getId();
    }

    public Long update(UsertypeEntity oUsertypeEntity) {
        oAuthService.OnlyAdmins();
        validate(oUsertypeEntity.getId());
        validate(oUsertypeEntity);
        oUsertypeRepository.save(oUsertypeEntity);
        return oUsertypeEntity.getId();
    }

    public Long delete(Long id) {
        oAuthService.OnlyAdmins();
        validate(id);
        oUsertypeRepository.deleteById(id);
        if (oUsertypeRepository.existsById(id)) {
            throw new ResourceNotModifiedException("can't remove register " + id);
        } else {
            return id;
        }
    }

}
