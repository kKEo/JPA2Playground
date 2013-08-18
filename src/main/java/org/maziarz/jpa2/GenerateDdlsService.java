package org.maziarz.jpa2;

import java.util.Map;

public interface GenerateDdlsService {

	String generate(Map<String,String> sources);

}
