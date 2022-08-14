package uralsib.liquiBase.helper.maker.refactor.sql;

import uralsib.liquiBase.helper.maker.changeset.dto.ChangsetFields;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class SqlRefactor {
    public void ChangeSql(String path)
    {
       String body=ReadFile(path);
       String body_new=TextRefactor(body);
       WriteFile(path,body_new);

    }

    public String ReadFile(String path)
    {
        String body="";
        File fileToRead = new File(path);
        try( FileReader fileStream = new FileReader( fileToRead );
             BufferedReader bufferedReader = new BufferedReader( fileStream ) ) {


            String line = null;
            while( (line = bufferedReader.readLine()) != null ) {
                body+=line+"\n";
            }

        } catch (Exception ex ) {
            System.out.println(ex.getMessage());
        }
        return body;
    }
    public String TextRefactor(String body)
    {
        StringBuilder body_new= new StringBuilder();
        boolean setBegin=false;
        boolean setIf=false;
        char[] arr=body.toCharArray();

        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i]==';' )
            {
                 if(!setBegin && !setIf && arr[i-1]!='\n')
                 {
                    body_new.append("\n;");
                 }
                 else
                 {
                     body_new.append(";");
                 }
            }
            else if ((arr[i]=='B'||arr[i]=='b')&&(arr[i+1]=='E'||arr[i+1]=='e')&&(arr[i+2]=='G'||arr[i+2]=='g')&&(arr[i+3]=='I'||arr[i+3]=='i')&&(arr[i+4]=='N'||arr[i+4]=='n')&&(arr[i+5]==' '||arr[i+5]=='\n'))
            {
                setBegin=true;
                body_new.append(arr[i]);
            }
            else if ((arr[i]=='I'||arr[i]=='i')&&(arr[i+1]=='F'||arr[i+1]=='f')&&(arr[i+2]==' '||arr[i+2]=='\n'))
            {
                if((arr[i-1]==' '||arr[i-1]=='\n')&&(arr[i-2]=='D'||arr[i-2]=='d')&&(arr[i-3]=='N'||arr[i-3]=='n') &&(arr[i-4]=='E'||arr[i-4]=='e') &&(arr[i-5]==' '||arr[i-5]=='\n'))
                {
                    setIf=false;
                }
                else
                {
                    setIf=true;
                }
                body_new.append(arr[i]);
            }
            else if ((arr[i]=='E'||arr[i]=='e')&&(arr[i+1]=='N'||arr[i+1]=='n')&&(arr[i+2]=='D'||arr[i+2]=='d')&&(arr[i+3]==' '||arr[i+3]=='\n'))
            {
                if (setIf)
                {
                    setIf=false;
                }
                else
                {
                    setBegin=false;
                }
                body_new.append(arr[i]);
            }
            else
            {
                body_new.append(arr[i]);
            }
        }
        return body_new.toString();
    }
    public void WriteFile(String path,String body)
    {
        try(FileWriter writer = new FileWriter(path+".txt", false))
        {
            // запись всей строки
            String text = body;
            writer.write(text);
            writer.flush();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}

