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
package es.blaster.consentino.api;

import es.blaster.consentino.entity.UsertypeEntity;
import es.blaster.consentino.service.UsertypeService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usertype")
public class UsertypeController {

    private final UsertypeService oUsertypeService;

    @Autowired
    public UsertypeController(UsertypeService oUsertypeService) {
        this.oUsertypeService = oUsertypeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsertypeEntity> get(@PathVariable Long id) {
        return new ResponseEntity<>(oUsertypeService.get(id), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oUsertypeService.count(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<UsertypeEntity>> getPage(
           @ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
           @RequestParam(name = "filter", required = false) String strFilter) {
        return new ResponseEntity<>(oUsertypeService.getPage(oPageable, strFilter), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody UsertypeEntity oUsertypeEntity) {
        return new ResponseEntity<Long>(oUsertypeService.create(oUsertypeEntity), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Long> update(@RequestBody UsertypeEntity oUsertypeEntity) {
        return new ResponseEntity<Long>(oUsertypeService.update(oUsertypeEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Long>(oUsertypeService.delete(id), HttpStatus.OK);
    }
}
