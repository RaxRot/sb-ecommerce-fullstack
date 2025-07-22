package com.raxrot.back.repoitories;

import com.raxrot.back.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
