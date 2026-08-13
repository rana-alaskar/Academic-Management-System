-- ====================================================================
-- Academic Management System - Database Schema
-- Oracle Database Schema Creation Script
-- ====================================================================

-- Drop existing tables if they exist (in reverse order due to foreign keys)
DROP TABLE GRADES CASCADE CONSTRAINTS;
DROP TABLE ENROLLMENTS CASCADE CONSTRAINTS;
DROP TABLE COURSES CASCADE CONSTRAINTS;
DROP TABLE USERS CASCADE CONSTRAINTS;

-- Drop sequences if they exist
DROP SEQUENCE USER_SEQ;
DROP SEQUENCE COURSE_SEQ;
DROP SEQUENCE ENROLLMENT_SEQ;
DROP SEQUENCE GRADE_SEQ;

-- ====================================================================
-- Create Sequences for Auto-Incrementing IDs
-- ====================================================================

CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE COURSE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE ENROLLMENT_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE GRADE_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- ====================================================================
-- USERS Table
-- Stores all users (Students, Instructors, Admins) with role-specific columns
-- ====================================================================

CREATE TABLE USERS (USER_ID NUMBER PRIMARY KEY, USERNAME VARCHAR2(50) UNIQUE NOT NULL, PASSWORD VARCHAR2(100) NOT NULL, ROLE VARCHAR2(20) NOT NULL CHECK (ROLE IN ('STUDENT', 'INSTRUCTOR', 'ADMIN')), FIRST_NAME VARCHAR2(50) NOT NULL, LAST_NAME VARCHAR2(50) NOT NULL, EMAIL VARCHAR2(100) UNIQUE NOT NULL, CREATED_DATE DATE DEFAULT SYSDATE, STUDENT_ID VARCHAR2(20) UNIQUE, MAJOR VARCHAR2(100), GPA NUMBER(3,2) CHECK (GPA >= 0 AND GPA <= 4), ENROLLMENT_YEAR NUMBER(4), TOTAL_CREDITS NUMBER DEFAULT 0, INSTRUCTOR_ID VARCHAR2(20) UNIQUE, DEPARTMENT VARCHAR2(100), OFFICE_LOCATION VARCHAR2(50), PHONE_EXTENSION VARCHAR2(10), SPECIALIZATION VARCHAR2(100), ADMIN_ID VARCHAR2(20) UNIQUE, ACCESS_LEVEL NUMBER DEFAULT 1);

-- ====================================================================
-- COURSES Table
-- Stores course information
-- ====================================================================

CREATE TABLE COURSES (COURSE_ID NUMBER PRIMARY KEY, COURSE_CODE VARCHAR2(20) UNIQUE NOT NULL, COURSE_NAME VARCHAR2(100) NOT NULL, CREDIT_HOURS NUMBER NOT NULL CHECK (CREDIT_HOURS >= 1 AND CREDIT_HOURS <= 6), INSTRUCTOR_ID NUMBER, SEMESTER VARCHAR2(20), YEAR NUMBER(4), MAX_CAPACITY NUMBER NOT NULL CHECK (MAX_CAPACITY > 0), CONSTRAINT FK_COURSE_INSTRUCTOR FOREIGN KEY (INSTRUCTOR_ID) REFERENCES USERS(USER_ID) ON DELETE SET NULL);

-- ====================================================================
-- ENROLLMENTS Table
-- Stores student course enrollments
-- ====================================================================

CREATE TABLE ENROLLMENTS (ENROLLMENT_ID NUMBER PRIMARY KEY, STUDENT_ID NUMBER NOT NULL, COURSE_ID NUMBER NOT NULL, ENROLLMENT_DATE DATE DEFAULT SYSDATE, STATUS VARCHAR2(20) DEFAULT 'ENROLLED' CHECK (STATUS IN ('ENROLLED', 'COMPLETED', 'DROPPED')), CONSTRAINT FK_ENROLLMENT_STUDENT FOREIGN KEY (STUDENT_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE, CONSTRAINT FK_ENROLLMENT_COURSE FOREIGN KEY (COURSE_ID) REFERENCES COURSES(COURSE_ID) ON DELETE CASCADE, CONSTRAINT UQ_STUDENT_COURSE UNIQUE (STUDENT_ID, COURSE_ID));

-- ====================================================================
-- GRADES Table
-- Stores student grades for enrolled courses
-- ====================================================================

CREATE TABLE GRADES (GRADE_ID NUMBER PRIMARY KEY, ENROLLMENT_ID NUMBER NOT NULL UNIQUE, GRADE VARCHAR2(5) NOT NULL CHECK (GRADE IN ('A', 'B+', 'B', 'C+', 'C', 'D+', 'D', 'F')), NUMERIC_GRADE NUMBER(5,2) NOT NULL CHECK (NUMERIC_GRADE >= 0 AND NUMERIC_GRADE <= 4), GRADE_DATE DATE DEFAULT SYSDATE, CONSTRAINT FK_GRADE_ENROLLMENT FOREIGN KEY (ENROLLMENT_ID) REFERENCES ENROLLMENTS(ENROLLMENT_ID) ON DELETE CASCADE);

-- ====================================================================
-- Schema Creation Complete
-- ====================================================================

COMMIT;
EXIT;
