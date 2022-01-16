package guess.service;

import guess.dao.CompanyDao;
import guess.domain.Language;
import guess.domain.source.Company;
import guess.util.LocalizationUtils;
import guess.util.SearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Company service implementation.
 */
@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyDao companyDao;

    @Autowired
    public CompanyServiceImpl(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    @Override
    public List<Company> getCompanies() {
        return companyDao.getCompanies();
    }

    @Override
    public List<Company> getCompanies(String name) {
        String trimmedLowerCasedName = SearchUtils.trimAndLowerCase(name);
        boolean isNameSet = SearchUtils.isStringSet(trimmedLowerCasedName);

        if (!isNameSet) {
            return Collections.emptyList();
        } else {
            return companyDao.getCompanies().stream()
                    .filter(c -> SearchUtils.isSubstringFound(trimmedLowerCasedName, c.getName()))
                    .toList();
        }
    }

    @Override
    public Company getCompanyById(long id) {
        return companyDao.getCompanyById(id);
    }

    @Override
    public List<Company> getCompaniesByIds(List<Long> ids) {
        return companyDao.getCompaniesByIds(ids);
    }

    @Override
    public List<Company> getCompaniesByFirstLetter(boolean isDigit, String firstLetter, Language language) {
        return companyDao.getCompanies().stream()
                .filter(c -> {
                    var name = LocalizationUtils.getString(c.getName(), language);
                    String nameFirstLetter;

                    if (name != null) {
                        String trimmedName = name.trim();
                        String nameWithFirstAlphaNumeric = SearchUtils.getSubStringWithFirstAlphaNumeric(trimmedName);

                        nameFirstLetter = (nameWithFirstAlphaNumeric.length() > 0) ? nameWithFirstAlphaNumeric.substring(0, 1) : null;
                    } else {
                        nameFirstLetter = null;
                    }

                    if (isDigit) {
                        return (nameFirstLetter != null) && Character.isDigit(nameFirstLetter.charAt(0));
                    } else {
                        return firstLetter.equalsIgnoreCase(nameFirstLetter);
                    }
                })
                .toList();
    }

    @Override
    public List<Company> getCompaniesByFirstLetters(String firstLetters, Language language) {
        String lowerCaseFirstLetters = (firstLetters != null) ? firstLetters.toLowerCase() : "";

        return companyDao.getCompanies().stream()
                .filter(c -> LocalizationUtils.getString(c.getName(), language).toLowerCase().indexOf(lowerCaseFirstLetters) == 0)
                .toList();
    }
}
