package com.example.demo.core.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TelephoneNameInfo {
    private List<TelephoneName> telephoneNameList;
}
