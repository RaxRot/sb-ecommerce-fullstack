package com.raxrot.back.controllers;

import com.raxrot.back.dtos.AddressDTO;
import com.raxrot.back.models.User;
import com.raxrot.back.services.AddressService;
import com.raxrot.back.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;
    public AddressController(AddressService addressService, AuthUtil authUtil) {
        this.addressService = addressService;
        this.authUtil = authUtil;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user=authUtil.loggedInUser();
        AddressDTO savedAddressDto=addressService.createAddress(addressDTO,user);
        return ResponseEntity.ok().body(savedAddressDto);
    }
}
