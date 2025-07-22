package com.raxrot.back.services;

import com.raxrot.back.dtos.AddressDTO;
import com.raxrot.back.models.User;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getAUserAddresses(User user);

    AddressDTO updateAddress(Long addressId, @Valid AddressDTO addressDTO);

    void deleteAddress(Long addressId);
}
