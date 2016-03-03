package dwf.geocode;

import dwf.persistence.embeddable.Address;

public interface ConsultaCEPService {
	Address[] consultaCEP(String cep);
}
