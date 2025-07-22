package com.raxrot.back.services.Impl;

import com.raxrot.back.dtos.AddressDTO;
import com.raxrot.back.models.Address;
import com.raxrot.back.models.User;
import com.raxrot.back.repoitories.AddressRepository;
import com.raxrot.back.services.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address=modelMapper.map(addressDTO, Address.class);
        List<Address>addresses=user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        address.setUser(user);
        Address savedAddress=addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }
}
