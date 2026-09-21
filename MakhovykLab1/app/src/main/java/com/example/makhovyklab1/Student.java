package com.example.makhovyklab1;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {

    public static final String[] GROUPS = {
            "КІ-231",
    };

    public static final int MAX_VARIANT = 20;

    private String firstName;
    private String lastName;
    private String group;

    public Student(String firstName, String lastName, String group) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
    }

    protected Student(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        group = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGroup() {
        return group;
    }

    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty()
                && lastName != null && !lastName.trim().isEmpty()
                && group != null && !group.trim().isEmpty();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(group);
    }
}