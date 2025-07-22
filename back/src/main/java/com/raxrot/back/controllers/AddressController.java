package com.raxrot.back.controllers;

import com.raxrot.back.dtos.AddressDTO;
import com.raxrot.back.models.User;
import com.raxrot.back.services.AddressService;
import com.raxrot.back.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO>addressDTOS=addressService.getAllAddresses();
        return ResponseEntity.ok().body(addressDTOS);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAllAddresses(@PathVariable Long addressId) {
        AddressDTO addressDTOS=addressService.getAddressById(addressId);
        return ResponseEntity.ok().body(addressDTOS);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        User user=authUtil.loggedInUser();
        List<AddressDTO>addressDTOS=addressService.getAUserAddresses(user);
        return ResponseEntity.ok().body(addressDTOS);
    }


}
