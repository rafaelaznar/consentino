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

import javax.servlet.http.HttpServletRequest;
import es.blaster.consentino.bean.UserBean;
import es.blaster.consentino.entity.UserEntity;
import es.blaster.consentino.exception.ResourceNotFoundException;
import es.blaster.consentino.exception.UnauthorizedException;
import es.blaster.consentino.helper.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.blaster.consentino.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private HttpServletRequest oRequest;

    @Autowired
    UserRepository oUserService;

    public String login(UserBean oUserBean) {
        if (oUserBean.getPassword() != null) {
            UserEntity oUserEntity = oUserService.findByUsernameAndPassword(oUserBean.getUsername(), oUserBean.getPassword());
            if (oUserEntity != null) {
                return JwtHelper.generateJWT(oUserEntity.getUsername(), oUserEntity.getUsertype().getName());
            } else {
                throw new UnauthorizedException("login or password incorrect");
            }
        } else {
            throw new UnauthorizedException("wrong password");
        }
    }

    public UserEntity check() {
        UserEntity oUserSessionEntity = oUserService.findByUsername((String) oRequest.getAttribute("user"));
        if (oUserSessionEntity != null) {
            return oUserSessionEntity;
        } else {
            throw new ResourceNotFoundException("no active session");
        }
    }

    public void OnlyAdmins() {
        UserEntity oUserSessionEntity = oUserService.findByUsername((String) oRequest.getAttribute("user"));
        if (oUserSessionEntity == null) {
            throw new UnauthorizedException("this request is only allowed to admin role");
        } else {
            if (oUserSessionEntity.getUsertype().getId() != 1) {
                throw new UnauthorizedException("this request is only allowed to admin role");
            }
        }
    }

}
