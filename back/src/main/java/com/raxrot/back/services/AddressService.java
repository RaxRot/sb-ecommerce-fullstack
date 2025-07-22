package com.raxrot.back.services;

import com.raxrot.back.dtos.AddressDTO;
import com.raxrot.back.models.User;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);
}
