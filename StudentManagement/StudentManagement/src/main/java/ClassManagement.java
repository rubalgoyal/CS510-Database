import jdk.jshell.spi.ExecutionControl;

import java.sql.*;

public class ClassManagement {

    public ActiveClass activeClass = null;

    public  void newClass(String courseNumber, String term, int sectionNumber, String classDescription, Connection conn) {
        boolean checkClassExist = util.checkClassExist(term, courseNumber, sectionNumber, conn);
        if (checkClassExist)
            System.out.println("Class already exist");
        else {
            String sqlQuery = "INSERT INTO class (course_number, term, section_number, class_description) VALUES (?,?,?,?)";

            try {
                conn.setAutoCommit(false);
                PreparedStatement statement = conn.prepareStatement(sqlQuery);
                statement.setString(1, courseNumber);
                statement.setString(2, term);
                statement.setInt(3, sectionNumber);
                statement.setString(4, classDescription);
                int rowInserted = statement.executeUpdate();
                if (rowInserted > 0)
                    System.out.println("New class successfully inserted");
                conn.commit();

            } catch (SQLException s) {
                throw new RuntimeException(s);
            }


        }
    }
    public void selectClass (String courseNumber, String term, Connection conn) {
        String sqlQuery = String.format(
                    """
                            SELECT class_id, course_number, term,section_number, class_description
                            FROM class
                            WHERE course_number = '%s'
                                AND term = '%s';
                    """
                    , courseNumber
                    ,term
            );
            try {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                int rowCount = util.getNumRows(resultSet);
                if(rowCount > 1)
                    System.out.println("There are more than 1 sections please specify section also");
                else {
                    resultSet = statement.executeQuery(sqlQuery);
                    assignActiveClass(resultSet);
                }

            } catch (SQLException s) {
                throw new RuntimeException(s);
            }

    }

    public void selectClass (String courseNumber, Connection conn) {
        //TODO implement most recent
        String sqlQuery = String.format(
                """
                        SELECT class_id, course_number, term,section_number, class_description
                        FROM class
                        WHERE course_number = '%s';
                            
                """
                , courseNumber

        );
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            int rowCount = util.getNumRows(resultSet);
            if(rowCount > 1)
                System.out.println("There are more than one courses please specify section and term also");
            else {
                resultSet = statement.executeQuery(sqlQuery);
                assignActiveClass(resultSet);
            }

        } catch (SQLException s) {
            throw new RuntimeException(s);
        }

    }


    public void selectClass (String courseNumber, String term, int sectionNumber, Connection conn) {
        boolean checkClassExist = util.checkClassExist(term, courseNumber, sectionNumber, conn);
        if (!checkClassExist)
            System.out.println("Class does not exist, Please enter correct class or create");
        else{
            String sqlQuery = String.format(
                    """
                            SELECT class_id, course_number, term,section_number, class_description
                            FROM class
                            WHERE course_number = '%s'
                                AND term = '%s'
                                AND section_number = %d;
                    """
                    , courseNumber
                    ,term
                    ,sectionNumber
            );
            try {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                assignActiveClass(resultSet);
            } catch (SQLException s) {
                throw new RuntimeException(s);
            }

        }

        //TODO will need overrides
    }

    public void listClasses () {
        //TODO implementation remaining
    }

    public void showClass () {
        if(activeClass == null)
            System.out.println("No active class at present");
        else {
            System.out.println("Class ID\tCourse Number\tTerm\tSection Number\tClass Description");
            System.out.println("------------------------------------------------------------------");
            System.out.println(activeClass.getClassID()+"\t\t\t"+activeClass.getCourseNumber()
                    +"\t\t\t"+activeClass.getTerm()+"\t\t\t"+activeClass.getSectionNumber()
                    +"\t\t\t"+activeClass.getClassDescription()
            );
        }
    }

    private void assignActiveClass(ResultSet resultSet){
        try{
            if(resultSet.next()){
                if(activeClass == null)
                    activeClass = new ActiveClass(
                            resultSet.getInt("class_id"),
                            resultSet.getString("course_number"),
                            resultSet.getString("term"),
                            resultSet.getInt("section_number"),
                            resultSet.getString("class_description")
                    );
                else{
                    activeClass.setClassID(resultSet.getInt("class_id"));
                    activeClass.setCourseNumber(resultSet.getString("course_number"));
                    activeClass.setTerm(resultSet.getString("term"));
                    activeClass.setSectionNumber(resultSet.getInt("section_number"));
                    activeClass.setClassDescription(resultSet.getString("class_description"));
                }
            }
        } catch (SQLException s) {
            throw new RuntimeException(s);
        }

    }


}

