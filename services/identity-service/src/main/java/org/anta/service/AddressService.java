package org.anta.service;

import lombok.RequiredArgsConstructor;
import org.anta.dto.request.AddressRequest;
import org.anta.dto.response.AddressResponse;
import org.anta.mapper.AddressMapper;
import org.anta.repository.AddressRepository;
import org.anta.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public List<AddressResponse> getAddressById(Long userId){

        var checked =  addressRepository.findByUserId(userId);

        if(checked.isEmpty()) {
            throw new RuntimeException("not found user id :" +userId);
        }
        return addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }


    @Transactional
    public AddressResponse add(Long userId, AddressRequest addressRequest){
        var user  = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var address = addressMapper.toEntity(addressRequest);
        address.setUser(user);

        var savedAddress = addressRepository.save(address);

        return addressMapper.toResponse(savedAddress);
    }

    @Transactional
    public AddressResponse update(Long addressId , Long userId, AddressRequest addressRequest){

        var address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or not yours"));


        addressMapper.updateFromRequest(addressRequest, address);

        var updatedAddress = addressRepository.save(address);

        return addressMapper.toResponse(updatedAddress);
    }

    @Transactional
    public void delete(Long addressId , Long userId){

        var address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or not yours"));
        addressRepository.delete(address);
    }

}
