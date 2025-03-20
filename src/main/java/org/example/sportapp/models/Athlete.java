package org.example.sportapp.models;

public class Athlete {
    private long id;
    private String second_name;
    private String first_name;
    private String patronymic;
    private String team_cast;
    private String position;
    private int title;
    private Long sport;
    private String rank;

    public Athlete(long id, String second_name, String first_name, String patronymic, String team_cast,
                   String position, int title, Long sport, String rank) {
        this.id = id;
        this.second_name = second_name;
        this.first_name = first_name;
        this.patronymic = patronymic;
        this.team_cast = team_cast;
        this.position = position;
        this.title = title;
        this.sport = sport;
        this.rank = rank;
    }

    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    public String getSecond_name(){return second_name;}
    public void setSecondName(String second_name){this.second_name = second_name;}

    public String getFirst_name(){return first_name;}
    public void setFirst_name(String first_name){this.first_name = first_name;}

    public String getPatronymic(){return patronymic;}
    public void setPatronymic(String patronymic){this.patronymic = patronymic;}

    public String getTeam_cast(){return team_cast;}
    public void setTeam_cast(String team_cast){this.team_cast = team_cast;}

    public String getPosition(){return position;}
    public void setPosition(String position){this.position = position;}

    public int getTitle(){return title;}
    public void setTitle(int title){this.title = title;}

    public Long getSport(){return sport;}
    public void setSport(Long sport){this.sport = sport;}

    public String getRank(){return rank;}
    public void setRank(String rank){this.rank = rank;}
}
