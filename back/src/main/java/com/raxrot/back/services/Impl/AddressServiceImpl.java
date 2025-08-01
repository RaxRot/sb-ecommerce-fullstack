package com.raxrot.back.services.Impl;

import com.raxrot.back.dtos.AddressDTO;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.models.Address;
import com.raxrot.back.models.User;
import com.raxrot.back.repoitories.AddressRepository;
import com.raxrot.back.services.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address>addresses=addressRepository.findAll();
        List<AddressDTO>addressDTOS=addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class)).collect(Collectors.toList());
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address=addressRepository.findById(addressId).orElseThrow(()->new ApiException("Address not found"));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAUserAddresses(User user) {
        List<Address>addresses=user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());


    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
       Address addressFromDb=addressRepository.findById(addressId).orElseThrow(()->new ApiException("Address not found"));
        addressFromDb.setStreet(addressDTO.getStreet());
        addressFromDb.setBuildingName(addressDTO.getBuildingName());
        addressFromDb.setCity(addressDTO.getCity());
        addressFromDb.setState(addressDTO.getState());
        addressFromDb.setCountry(addressDTO.getCountry());
        addressFromDb.setPincode(addressDTO.getPincode());
        Address savedAddress=addressRepository.save(addressFromDb);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public void deleteAddress(Long addressId) {
        if (addressRepository.existsById(addressId)) {
            addressRepository.deleteById(addressId);
        }else{
            throw new ApiException("Address not found");
        }
    }
}
