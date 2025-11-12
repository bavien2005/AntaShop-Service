package org.anta.controller;


import lombok.RequiredArgsConstructor;
import org.anta.dto.request.AddressRequest;
import org.anta.dto.response.AddressResponse;
import org.anta.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/allUserAddress/{userId}")
    public ResponseEntity<Map<List<AddressResponse> , String>> getAllAddressByUserId(
            @PathVariable  Long userId)
    {
       return ResponseEntity.ok(Map.of(addressService.getAddressById(userId) ,
               "Get all address successfully"));
    }


    @PostMapping("/add/{userId}")
    public ResponseEntity<Map<AddressResponse, String>> addAddress(
            @PathVariable Long userId,
            @RequestBody AddressRequest addressRequest)
    {
        return ResponseEntity.ok(Map.of(addressService.add(userId , addressRequest) , "" +
                "Add address successfully " + " for user: " +userId));
    }


    @PutMapping("/update/addressId/{addressId}/userId/{userId}")
    public ResponseEntity<Map<AddressResponse, String>> updateAddress(
            @PathVariable Long addressId ,
            @PathVariable Long userId,
            @RequestBody AddressRequest addressRequest)
    {
        return ResponseEntity.ok(Map.of(addressService.update(addressId , userId , addressRequest) ,
                " " + "Update address successfully" + addressId +" for user " + userId));
    }


    @DeleteMapping("delete/addressId/{addressId}/userId/{userId}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Long addressId ,
            @PathVariable Long userId)
    {
        addressService.delete(addressId , userId);
        return ResponseEntity.ok("Delete address successfully" + addressId +" for user " + userId);
    }

}
