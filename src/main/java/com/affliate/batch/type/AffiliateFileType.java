package com.affliate.batch.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AffiliateFileType {

    FLAT ("data"),
    TXT ("txt"),
    CSV ("csv"),
    XML ("xml"),
    TSV ("tsv"),
    JSON ("json"),
    IN ("in"),
    ZIP ("zip");

    private final String extension;

}
