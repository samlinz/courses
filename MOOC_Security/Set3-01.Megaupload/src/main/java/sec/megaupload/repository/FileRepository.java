package sec.megaupload.repository;

import java.util.List;
import sec.megaupload.domain.FileObject;
import org.springframework.data.jpa.repository.JpaRepository;
import sec.megaupload.domain.Account;

public interface FileRepository extends JpaRepository<FileObject, Long> {

    List<FileObject> findByAccount(Account account);

}
