package org.anta.controller;


import lombok.RequiredArgsConstructor;
import org.anta.dto.request.AddressRequest;
import org.anta.dto.response.AddressResponse;
import org.anta.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/allUserAddress/{userId}")
    public ResponseEntity<List<AddressResponse>> getAllAddressByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressById(userId));
    }


    @PostMapping("/add/{userId}")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable Long userId,
            @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.add(userId, addressRequest));
    }

    @PutMapping("/setDefault/{addressId}/user/{userId}")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @PathVariable Long addressId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(addressService.setDefaultAddress(addressId, userId));
    }


    @PutMapping("/update/addressId/{addressId}/userId/{userId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @PathVariable Long userId,
            @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.update(addressId, userId, addressRequest));
    }


    @DeleteMapping("delete/addressId/{addressId}/userId/{userId}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Long addressId,
            @PathVariable Long userId) {
        addressService.delete(addressId, userId);
        return ResponseEntity.ok("Delete address successfully" + addressId + " for user " + userId);
    }

}
