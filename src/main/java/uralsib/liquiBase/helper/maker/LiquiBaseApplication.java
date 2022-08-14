package uralsib.liquiBase.helper.maker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import uralsib.liquiBase.helper.maker.changeset.ChangesetMaker;
import uralsib.liquiBase.helper.maker.refactor.sql.SqlRefactor;

import java.io.File;

@SpringBootApplication
public class LiquiBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiquiBaseApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
//		ChangesetMaker changesetMaker=new ChangesetMaker();
//		File dir = new File("F:/test");
//		File[] arrFiles = dir.listFiles();
//		for (File arrFile: arrFiles)
//		{
//			changesetMaker.MakeChangesets(arrFile.getAbsolutePath());
//		}
		SqlRefactor sql = new SqlRefactor();
		sql.ChangeSql("F:\\test\\table\\01-create-invest-test.sql");
	}
}
