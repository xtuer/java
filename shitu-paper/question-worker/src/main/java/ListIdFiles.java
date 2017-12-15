import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class ListIdFiles {
    public static void main(String[] args) {
        File holder = new File("/Users/Biao/Documents/套卷/题目ID");
        File[] subjects = holder.listFiles((d, name) -> {
            return !name.equals("readme.txt");
        });

        for (File subject : subjects) {
            File[] idFiles = subject.listFiles();
            for (File idFile : idFiles) {
                String subjectLabel = subject.getName();
                String subjectCode = FilenameUtils.getBaseName(idFile.getName());
                System.out.printf("{\"%s\", \"F:/题目ID/%s/%s.csv\", \"F:/题目/%s/%s\"},\n",
                        subjectCode, subjectLabel, subjectCode, subjectLabel, subjectCode);
            }
        }
    }
}
