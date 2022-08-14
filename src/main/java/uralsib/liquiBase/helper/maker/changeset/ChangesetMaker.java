package uralsib.liquiBase.helper.maker.changeset;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import uralsib.liquiBase.helper.maker.changeset.dto.ChangsetFields;

public class ChangesetMaker {
    public void MakeChangesets(String path)
    {
        //Считать все файлы
        File dir = new File(path);
        File[] arrFiles = dir.listFiles();
        List<ChangsetFields> ls_creation= getCreation(arrFiles);
        List<ChangsetFields> ls_inserts = getInsert(arrFiles,ls_creation);
        List<ChangsetFields> ls_delete=  getDelete(arrFiles,ls_inserts);
        makeFile(path,ls_delete);
    }
    private   List<ChangsetFields> getCreation(File[] arrFiles )
    {
        //Находим все инстансы creation
        List<ChangsetFields> ls_creation = new ArrayList<>();
        for (File arrFile : arrFiles)
        {
            String[] splitedName = arrFile.getName().split("-");
            if(Objects.equals(splitedName[1], "create")) {
                ChangsetFields cf = new ChangsetFields();
                cf.createFileName=arrFile.getName();
                cf.number=splitedName[0];
                String objName="";
                for (int j = 2; j < splitedName.length; j++) {
                    if (j==2)
                    {
                        objName=splitedName[j];
                    }
                    else
                    {
                        objName=objName+"-"+splitedName[j];
                    }
                }
                cf.ObjectName=objName;
                ls_creation.add(cf);
            }
        }
        return ls_creation;
    }
    private   List<ChangsetFields> getInsert(File[] arrFiles,List<ChangsetFields> ls_creation )
    {
        //Присоединяем insert
        List<ChangsetFields> ls_Middle= new ArrayList<>();
        for (ChangsetFields cf_current : ls_creation)
        {
            ChangsetFields cf =cf_current;
            for (File arrFile : arrFiles)
            {
                String[] splitedName = arrFile.getName().split("-");
                if(Objects.equals(splitedName[1], "insert") ||Objects.equals(splitedName[1], "increment"))
                {
                    cf.insertFileName=arrFile.getName();
                }
            }
            ls_Middle.add(cf);
        }
        return ls_Middle;
    }
    private   List<ChangsetFields> getDelete(File[] arrFiles,List<ChangsetFields> ls_inserts )
    {
        //Присоединяем delete
        List<ChangsetFields> ls_Final= new ArrayList<>();
        for (ChangsetFields cf_current : ls_inserts)
        {
            ChangsetFields cf =cf_current;
            for (File arrFile : arrFiles)
            {
                String[] splitedName = arrFile.getName().split("-");
                if(Objects.equals(splitedName[1], "delete"))
                {
                    cf.deleteFileName=arrFile.getName();
                }
            }
            ls_Final.add(cf);
        }
        return ls_Final;
    }
    private   void makeFile(String path,List<ChangsetFields> ls_inserts )
    {
        for (ChangsetFields cf_local : ls_inserts)
        {
            String number=cf_local.number;
            //Имя файла
            String fileName=number+"-changeset-"+cf_local.ObjectName;
            //Тело Changeset - заголовок
            String bodyHead="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<databaseChangeLog\n" +
                    "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog\n" +
                    "                      http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd\">\n" +
                    "\n" +
                    "    <changeSet id=\"";
            //Номер Changset
            String numberChangeSet=number;
            if(numberChangeSet.startsWith("0"))
            {
                numberChangeSet=number.substring(1);
            }
            //Конец заголовка тела
            String bodyHeadPreCreate="\" author=\"TrofimovAn\">\n";
            //Создание объекта
            String bodyCreate="";
            if(cf_local.createFileName!=null)
            {
                bodyCreate="        <sqlFile dbms=\"oracle\"\n" +
                        "                 encoding=\"utf8\"\n" +
                        "                 endDelimiter=\";\"\n" +
                        "                 relativeToChangelogFile=\"true\"\n" +
                        "                 splitStatements=\"true\"\n" +
                        "                 stripComments=\"true\"\n" +
                        "                 path=\"" + cf_local.createFileName +"\"/>\n";
            }
            //Наполнение объекта
            String bodyInsert="";
            if(cf_local.insertFileName!=null)
            {
            bodyInsert="        <sqlFile dbms=\"oracle\"\n" +
                    "                 encoding=\"utf8\"\n" +
                    "                 endDelimiter=\";\"\n" +
                    "                 relativeToChangelogFile=\"true\"\n" +
                    "                 splitStatements=\"true\"\n" +
                    "                 stripComments=\"true\"\n" +
                    "                 path=\""+cf_local.insertFileName+"/>\n";
            }
            //Удаление объекта в Rollback
            String bodyDelete="";
            if(cf_local.deleteFileName!=null)
            {
                bodyDelete="        <rollback>\n" +
                        "            <sqlFile dbms=\"oracle\"\n" +
                        "                     encoding=\"utf8\"\n" +
                        "                     endDelimiter=\";\"\n" +
                        "                     relativeToChangelogFile=\"true\"\n" +
                        "                     splitStatements=\"true\"\n" +
                        "                     stripComments=\"true\"\n" +
                        "                     path=\""+cf_local.deleteFileName+"/>\n" +
                        "        </rollback>\n";
            }
            String bodyEnd="    </changeSet>\n" +
                    "</databaseChangeLog>";
            //Формирование файла
            String body= new StringBuilder().append(bodyHead).append(numberChangeSet).append(bodyHeadPreCreate).append(bodyCreate).append(bodyInsert).append(bodyDelete).append(bodyEnd).toString();
            try {
                List<String> lines = Arrays.asList(body);
                Path file = Paths.get(path+"/"+fileName+".xml");
                Files.write(file, lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
