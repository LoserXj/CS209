import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

  List<Course> courses = new ArrayList<>();

  public OnlineCoursesAnalyzer(String datasetPath) {
    BufferedReader br = null;
    String line;
    try {
      br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
        Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
            Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
            Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
            Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
            Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
            Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
            Double.parseDouble(info[21]), Double.parseDouble(info[22]));
        courses.add(course);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * @author xujian
   * @return Map<String, Integer>
   */
  public Map<String, Integer> getPtcpCountByInst() {
    return courses.stream().collect(Collectors.groupingBy(Course::getInstitution,
            Collectors.summingInt(Course::getParticipants)));
  }

  /**
   * @author xujian
   * @return Map<String, Integer>
   */
  public Map<String, Integer> getPtcpCountByInstAndSubject() {
    Map<String, Integer> map = courses.stream().collect(Collectors.groupingBy(e ->
            e.getInstitution() + "-"
            + e.getSubject(), Collectors.summingInt(Course::getParticipants)));
    List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
    list.sort(new Comparator<Map.Entry<String, Integer>>() {
      @Override
      public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        if (o1.getValue() > o2.getValue()) {
          return -1;
        } else if (o1.getValue() == o2.getValue()) {
          return -o1.getKey().compareTo(o2.getKey());
        } else {
            return 1;
        }
        }
      }
    );
    Map<String, Integer> returnMap = new LinkedHashMap<>();
    for (int i = 0; i < list.size(); i++) {
      returnMap.put(list.get(i).getKey(), list.get(i).getValue());
    }
    return returnMap;
  }

  /**
   * @author xujian
   * @return Map<String, List<List<String>>>
   */
  public Map<String, List<List<String>>> getCourseListOfInstructor() {
    Map<String, List<List<String>>> returnMap = new HashMap<>();
    courses.forEach(course -> {
      String[] instructorList = course.getInstructors().split(",");
      int len = instructorList.length;
      for (int i = 0; i < len; i++) {
        instructorList[i] = instructorList[i].trim();
        if (!returnMap.containsKey(instructorList[i])) {
          List<List<String>> list = new ArrayList<>();
          List<String> list1 = new ArrayList<>();
          List<String> list2 = new ArrayList<>();
          list.add(list1);
          list.add(list2);
          returnMap.put(instructorList[i], list);
        }
        if (len > 1) {
          if (!returnMap.get(instructorList[i]).get(1).contains(course.getTitle())) {
            returnMap.get(instructorList[i]).get(1).add(course.getTitle());
          }
        }
        if (len == 1) {
          if (!returnMap.get(instructorList[i]).get(0).contains(course.getTitle())) {
            returnMap.get(instructorList[i]).get(0).add(course.getTitle());
          }
        }
      }
    });
    returnMap.forEach((k, v) -> {
      Collections.sort(v.get(0));
      Collections.sort(v.get(1));
        }
    );
    return returnMap;
  }

  /**
   * @author xujian
   * @param topK
   * @param by
   * @return List<String>
   */
  public List<String> getCourses(int topK, String by) {
    List<String> list = new ArrayList<>();
    if (by.equals("hours")) {
      Map<String, Double> map = new HashMap<>();
      courses.forEach(c -> {
        if (!map.containsKey(c.getTitle())) {
          map.put(c.getTitle(), c.getTotalHours());
        } else {
          map.put(c.getTitle(), Math.max(map.get(c.getTitle()), c.getTotalHours()));
        }
      }
      );
      List<Map.Entry<String, Double>> list1 = new ArrayList<>(map.entrySet());
      list1.sort(new Comparator<Map.Entry<String, Double>>() {
        @Override
        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
          if (o1.getValue() > o2.getValue()) {
            return -1;
          } else if (o1.getValue() == o2.getValue()) {
            return -o1.getKey().compareTo(o2.getKey());
          } else {
            return 1;
          }
          }
        }
      );
      for (int i = 0; i < list1.size() && i < topK; i++) {
        list.add(list1.get(i).getKey());
      }
    } else if (by.equals("participants")) {
      Map<String, Integer> map = new HashMap<>();
      courses.forEach(c -> {
        if (!map.containsKey(c.getTitle())) {
              map.put(c.getTitle(), c.getParticipants());
        } else {
              map.put(c.getTitle(), Math.max(map.get(c.getTitle()), c.getParticipants()));
            }
          }
      );
      List<Map.Entry<String, Integer>> list1 = new ArrayList<>(map.entrySet());
      list1.sort(new Comparator<Map.Entry<String, Integer>>() {
          @Override
          public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            if (o1.getValue() > o2.getValue()) {
              return -1;
          } else if (o1.getValue() == o2.getValue()) {
              return -o1.getKey().compareTo(o2.getKey());
          } else {
              return 1;
            }
          }
        }
      );
      for (int i = 0; i < list1.size() && i < topK; i++) {
        list.add(list1.get(i).getKey());
      }
    }
    System.out.println(list.toString());
    return list;
  }


  /**
   * @author xujian
   * @param courseSubject
   * @param percentAudited
   * @param totalCourseHours
   * @return List<String>
  */
  public List<String> searchCourses(String courseSubject, double percentAudited,
                                    double totalCourseHours) {
    List<String> list = new ArrayList<>();
    Pattern pattern = Pattern.compile(courseSubject, Pattern.CASE_INSENSITIVE);
    courses.forEach(c -> {
          if (pattern.matcher(c.getSubject()).find() && c.getPercentAudited() >= percentAudited
                    && c.getTotalHours() <= totalCourseHours) {
            if (!list.contains(c.getTitle())) {
              list.add(c.getTitle());
            }
          }
        }
    );
    Collections.sort(list);
    return list;
  }

  /**
   * @author xujian
   * @param age
   * @param gender
   * @param isBachelorOrHigher
   * @return List<String>
   */
  public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
    Map<String, Double> averAgeMap = courses.stream().collect(Collectors.groupingBy(
            Course::getNumber, Collectors.averagingDouble(Course::getMedianAge)));
    Map<String, Double> averMaleMap = courses.stream().collect(Collectors.groupingBy(
            Course::getNumber, Collectors.averagingDouble(Course::getPercentMale)));
    Map<String, Double> averBachMap = courses.stream().collect(Collectors.groupingBy(
            Course::getNumber, Collectors.averagingDouble(Course::getPercentDegree)));
    Map<String, Course> newestCourseMap = new HashMap<>();
    courses.forEach(e -> {
          if (!newestCourseMap.containsKey(e.number)) {
            newestCourseMap.put(e.number, e);
          } else {
            Course newestCourse = newestCourseMap.get(e.number).launchDate.compareTo(e.launchDate)
                >= 0 ? newestCourseMap.get(e.number) : e;
            newestCourseMap.put(e.number, newestCourse);
          }
        }
    );
    Map<String, Double> returnMap = new HashMap<>();
    newestCourseMap.forEach((k, v) -> {
          double sim = Math.pow(age - averAgeMap.get(k), 2) + Math.pow(
              100 * gender - averMaleMap.get(k), 2)
              + Math.pow(100 * isBachelorOrHigher - averBachMap.get(k), 2);
          returnMap.put(v.getTitle(), sim);
        }
    );
    List<String> list = new ArrayList<>();
    returnMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
    .sorted(Map.Entry.comparingByValue()).forEach(e -> list.add(e.getKey()));
    List<String> returnList = new ArrayList<>();
    list.stream().limit(10).forEach(e -> returnList.add(e));
    return returnList;
  }

}

class Course {
  String institution;
  String number;
  Date launchDate;
  String title;
  String instructors;
  String subject;
  int year;
  int honorCode;
  int participants;
  int audited;
  int certified;
  double percentAudited;
  double percentCertified;
  double percentCertified50;
  double percentVideo;
  double percentForum;
  double gradeHigherZero;
  double totalHours;
  double medianHoursCertification;
  double medianAge;
  double percentMale;
  double percentFemale;
  double percentDegree;

  public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
    this.institution = institution;
    this.number = number;
    this.launchDate = launchDate;
    if (title.startsWith("\"")) {
      title = title.substring(1);
    }
    if (title.endsWith("\"")) {
      title = title.substring(0, title.length() - 1);
    }
    this.title = title;
    if (instructors.startsWith("\"")) {
      instructors = instructors.substring(1);
    }
    if (instructors.endsWith("\"")) {
      instructors = instructors.substring(0, instructors.length() - 1);
    }
    this.instructors = instructors;
    if (subject.startsWith("\"")) {
      subject = subject.substring(1);
    }
    if (subject.endsWith("\"")) {
      subject = subject.substring(0, subject.length() - 1);
    }
    this.subject = subject;
    this.year = year;
    this.honorCode = honorCode;
    this.participants = participants;
    this.audited = audited;
    this.certified = certified;
    this.percentAudited = percentAudited;
    this.percentCertified = percentCertified;
    this.percentCertified50 = percentCertified50;
    this.percentVideo = percentVideo;
    this.percentForum = percentForum;
    this.gradeHigherZero = gradeHigherZero;
    this.totalHours = totalHours;
    this.medianHoursCertification = medianHoursCertification;
    this.medianAge = medianAge;
    this.percentMale = percentMale;
    this.percentFemale = percentFemale;
    this.percentDegree = percentDegree;
  }

  public String getInstitution() {
    return institution;
  }

  public String getNumber() {
    return number;
  }

  public Date getLaunchDate() {
    return launchDate;
  }

  public String getTitle() {
    return title;
  }

  public String getInstructors() {
    return instructors;
  }

  public String getSubject() {
    return subject;
  }

  public int getYear() {
    return year;
  }

  public int getHonorCode() {
    return honorCode;
  }

  public int getParticipants() {
    return participants;
  }

  public int getAudited() {
    return audited;
  }

  public int getCertified() {
    return certified;
  }

  public double getPercentAudited() {
    return percentAudited;
  }

  public double getPercentCertified() {
    return percentCertified;
  }

  public double getPercentCertified50() {
    return percentCertified50;
  }

  public double getPercentVideo() {
    return percentVideo;
  }

  public double getPercentForum() {
    return percentForum;
  }

  public double getGradeHigherZero() {
    return gradeHigherZero;
  }

  public double getTotalHours() {
    return totalHours;
  }

  public double getMedianHoursCertification() {
    return medianHoursCertification;
  }

  public double getMedianAge() {
    return medianAge;
  }

  public double getPercentMale() {
    return percentMale;
  }

  public double getPercentFemale() {
    return percentFemale;
  }

  public double getPercentDegree() {
    return percentDegree;
  }
}