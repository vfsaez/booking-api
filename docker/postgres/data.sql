--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2 (Debian 16.2-1.pgdg120+2)
-- Dumped by pg_dump version 16.2 (Debian 16.2-1.pgdg120+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: tb_course; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_course (
                                  id bigint NOT NULL,
                                  name character varying(255) NOT NULL,
                                  professor_id bigint NOT NULL
);


ALTER TABLE public.tb_course OWNER TO postgres;

--
-- Name: tb_course_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_course_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_course_id_seq OWNER TO postgres;

--
-- Name: tb_course_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_course_id_seq OWNED BY public.tb_course.id;


--
-- Name: tb_result; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_result (
                                  id bigint NOT NULL,
                                  grade integer NOT NULL,
                                  course_id bigint NOT NULL,
                                  professor_id bigint NOT NULL,
                                  student_id bigint
);


ALTER TABLE public.tb_result OWNER TO postgres;

--
-- Name: tb_result_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_result_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_result_id_seq OWNER TO postgres;

--
-- Name: tb_result_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_result_id_seq OWNED BY public.tb_result.id;


--
-- Name: tb_student; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_student (
                                   id bigint NOT NULL,
                                   date_of_birth timestamp without time zone,
                                   email character varying(255) NOT NULL,
                                   family_name character varying(255) NOT NULL,
                                   name character varying(255) NOT NULL,
                                   professor_id bigint NOT NULL
);


ALTER TABLE public.tb_student OWNER TO postgres;

--
-- Name: tb_student_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_student_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_student_id_seq OWNER TO postgres;

--
-- Name: tb_student_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_student_id_seq OWNED BY public.tb_student.id;


--
-- Name: tb_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_user (
                                id bigint NOT NULL,
                                name character varying(255),
                                password character varying(255),
                                roles character varying(255),
                                username character varying(255)
);


ALTER TABLE public.tb_user OWNER TO postgres;

--
-- Name: tb_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_user_id_seq OWNER TO postgres;

--
-- Name: tb_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_user_id_seq OWNED BY public.tb_user.id;


--
-- Name: tb_course id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_course ALTER COLUMN id SET DEFAULT nextval('public.tb_course_id_seq'::regclass);


--
-- Name: tb_result id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_result ALTER COLUMN id SET DEFAULT nextval('public.tb_result_id_seq'::regclass);


--
-- Name: tb_student id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_student ALTER COLUMN id SET DEFAULT nextval('public.tb_student_id_seq'::regclass);


--
-- Name: tb_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_user ALTER COLUMN id SET DEFAULT nextval('public.tb_user_id_seq'::regclass);


--
-- Data for Name: tb_course; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_course (id, name, professor_id) FROM stdin;
1	Mathematics	1
2	Applied Sciences	2
\.


--
-- Data for Name: tb_result; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_result (id, grade, course_id, professor_id, student_id) FROM stdin;
1	0	1	1	1
2	1	1	1	2
3	2	2	2	3
4	3	2	2	4
\.


--
-- Data for Name: tb_student; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_student (id, date_of_birth, email, family_name, name, professor_id) FROM stdin;
1	2000-02-29 21:55:09.17	john@stringcollege.com	DemoString	John	1
3	1998-02-28 21:55:09.17	dean@charlescollege.com	DemoCharles	Dean	2
2	1999-02-28 21:55:09.17	beth@stringcollege.com	DemoString	Beth	1
4	1997-02-28 21:55:09.17	mary@charlescollege.com	DemoCharles	Mary	2
\.


--
-- Data for Name: tb_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_user (id, name, password, roles, username) FROM stdin;
3	Principal Donovan	$2a$10$1fTsKGQgaY3mLL7iU5WtxuQXI2ZAIVPLaAChmP0DGYZT8HZWB4GAm	ADMIN	donovan
2	Professor Charles	$2a$10$1fTsKGQgaY3mLL7iU5WtxuQXI2ZAIVPLaAChmP0DGYZT8HZWB4GAm	USER	charles
1	Professor String	$2a$10$1fTsKGQgaY3mLL7iU5WtxuQXI2ZAIVPLaAChmP0DGYZT8HZWB4GAm	USER	string
\.


--
-- Name: tb_course_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_course_id_seq', 2, true);


--
-- Name: tb_result_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_result_id_seq', 4, true);


--
-- Name: tb_student_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_student_id_seq', 4, true);


--
-- Name: tb_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_user_id_seq', 3, true);


--
-- Name: tb_course tb_course_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_course
    ADD CONSTRAINT tb_course_pkey PRIMARY KEY (id);


--
-- Name: tb_result tb_result_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_result
    ADD CONSTRAINT tb_result_pkey PRIMARY KEY (id);


--
-- Name: tb_student tb_student_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_student
    ADD CONSTRAINT tb_student_pkey PRIMARY KEY (id);


--
-- Name: tb_user tb_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_user
    ADD CONSTRAINT tb_user_pkey PRIMARY KEY (id);


--
-- Name: tb_user uk_4wv83hfajry5tdoamn8wsqa6x; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_user
    ADD CONSTRAINT uk_4wv83hfajry5tdoamn8wsqa6x UNIQUE (username);


--
-- Name: tb_student fk4dx3lda1671ckqh8eax5rs4p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_student
    ADD CONSTRAINT fk4dx3lda1671ckqh8eax5rs4p FOREIGN KEY (professor_id) REFERENCES public.tb_user(id);


--
-- Name: tb_result fkialkewwnrym0acfqmm3xl8j5i; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_result
    ADD CONSTRAINT fkialkewwnrym0acfqmm3xl8j5i FOREIGN KEY (professor_id) REFERENCES public.tb_user(id);


--
-- Name: tb_course fkjyc9xu0uy1tuuon44sal2kwxx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_course
    ADD CONSTRAINT fkjyc9xu0uy1tuuon44sal2kwxx FOREIGN KEY (professor_id) REFERENCES public.tb_user(id);


--
-- Name: tb_result fkltomx0ac1jej79oc81wv4opyf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_result
    ADD CONSTRAINT fkltomx0ac1jej79oc81wv4opyf FOREIGN KEY (course_id) REFERENCES public.tb_course(id);


--
-- Name: tb_result fktq3sr2n2sy9fuvafcdur53588; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_result
    ADD CONSTRAINT fktq3sr2n2sy9fuvafcdur53588 FOREIGN KEY (student_id) REFERENCES public.tb_student(id);


--
-- PostgreSQL database dump complete
--

