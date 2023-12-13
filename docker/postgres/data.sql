--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1 (Debian 16.1-1.pgdg120+1)
-- Dumped by pg_dump version 16.1 (Debian 16.1-1.pgdg120+1)

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
-- Name: tb_block; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_block (
                                 id bigint NOT NULL,
                                 end_date timestamp without time zone NOT NULL,
                                 start_date timestamp without time zone NOT NULL,
                                 status integer NOT NULL,
                                 property_id bigint,
                                 owner_id bigint NOT NULL
);


ALTER TABLE public.tb_block OWNER TO postgres;

--
-- Name: tb_block_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_block_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_block_id_seq OWNER TO postgres;

--
-- Name: tb_block_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_block_id_seq OWNED BY public.tb_block.id;


--
-- Name: tb_booking; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_booking (
                                   id bigint NOT NULL,
                                   end_date timestamp without time zone NOT NULL,
                                   price double precision,
                                   start_date timestamp without time zone NOT NULL,
                                   status integer NOT NULL,
                                   client_id bigint,
                                   property_id bigint,
                                   owner_id bigint
);


ALTER TABLE public.tb_booking OWNER TO postgres;

--
-- Name: tb_booking_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_booking_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_booking_id_seq OWNER TO postgres;

--
-- Name: tb_booking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_booking_id_seq OWNED BY public.tb_booking.id;


--
-- Name: tb_client; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_client (
                                  id bigint NOT NULL,
                                  name character varying(255) NOT NULL,
                                  total_rentals integer NOT NULL,
                                  owner_id bigint
);


ALTER TABLE public.tb_client OWNER TO postgres;

--
-- Name: tb_client_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_client_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_client_id_seq OWNER TO postgres;

--
-- Name: tb_client_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_client_id_seq OWNED BY public.tb_client.id;


--
-- Name: tb_property; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tb_property (
                                    id bigint NOT NULL,
                                    name character varying(255) NOT NULL,
                                    price double precision NOT NULL,
                                    owner_id bigint
);


ALTER TABLE public.tb_property OWNER TO postgres;

--
-- Name: tb_property_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tb_property_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tb_property_id_seq OWNER TO postgres;

--
-- Name: tb_property_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.tb_property_id_seq OWNED BY public.tb_property.id;


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
-- Name: tb_block id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_block ALTER COLUMN id SET DEFAULT nextval('public.tb_block_id_seq'::regclass);


--
-- Name: tb_booking id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_booking ALTER COLUMN id SET DEFAULT nextval('public.tb_booking_id_seq'::regclass);


--
-- Name: tb_client id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_client ALTER COLUMN id SET DEFAULT nextval('public.tb_client_id_seq'::regclass);


--
-- Name: tb_property id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_property ALTER COLUMN id SET DEFAULT nextval('public.tb_property_id_seq'::regclass);


--
-- Name: tb_user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_user ALTER COLUMN id SET DEFAULT nextval('public.tb_user_id_seq'::regclass);


--
-- Data for Name: tb_block; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_block (id, end_date, start_date, status, property_id, owner_id) FROM stdin;
\.


--
-- Data for Name: tb_booking; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_booking (id, end_date, price, start_date, status, client_id, property_id, owner_id) FROM stdin;
5	2023-11-17 10:10:57.205	1500	2023-11-17 10:10:57.205	0	1	1	1
4	2023-12-17 10:10:57.205	1500	2023-12-17 10:10:57.205	0	1	1	1
2	2023-12-17 10:10:57.205	1500	2023-12-17 10:10:57.205	1	1	1	1
3	2023-12-17 10:10:57.205	1500	2023-12-17 10:10:57.205	1	1	1	1
1	2023-12-17 10:10:57.205	1500	2023-12-17 10:10:57.205	1	1	1	1
6	2023-11-30 10:10:57.205	1500	2023-11-30 10:10:57.205	0	1	1	1
16	2023-04-13 15:50:26.788	1500	2023-04-13 15:50:26.788	0	1	1	1
14	2023-09-13 13:23:34.257	500	2023-09-13 13:23:34.257	0	1	1	1
13	2023-10-13 13:23:34.257	500	2023-10-13 13:23:34.257	0	1	1	1
12	2023-10-13 13:23:34.257	500	2023-10-13 13:23:34.257	0	1	1	1
10	2023-10-13 13:23:34.257	500	2023-10-13 13:23:34.257	0	1	1	1
11	2023-10-13 13:23:34.257	500	2023-10-13 13:23:34.257	0	1	1	1
9	2023-10-13 13:23:34.257	500	2023-10-13 13:23:34.257	0	1	1	1
8	2023-10-13 13:23:34.257	500	2023-10-13 13:23:34.257	0	1	1	1
7	2023-12-13 13:23:34.257	500	2023-12-13 13:23:34.257	0	1	1	1
\.


--
-- Data for Name: tb_client; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_client (id, name, total_rentals, owner_id) FROM stdin;
1	Client 1	0	1
\.


--
-- Data for Name: tb_property; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_property (id, name, price, owner_id) FROM stdin;
1	Mansion	1500	1
\.


--
-- Data for Name: tb_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tb_user (id, name, password, roles, username) FROM stdin;
2	admin	$2a$10$1fTsKGQgaY3mLL7iU5WtxuQXI2ZAIVPLaAChmP0DGYZT8HZWB4GAm	ADMIN	admin
1	user	$2a$10$1fTsKGQgaY3mLL7iU5WtxuQXI2ZAIVPLaAChmP0DGYZT8HZWB4GAm	USER	user
\.


--
-- Name: tb_block_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_block_id_seq', 1, false);


--
-- Name: tb_booking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_booking_id_seq', 16, true);


--
-- Name: tb_client_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_client_id_seq', 2, true);


--
-- Name: tb_property_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_property_id_seq', 2, true);


--
-- Name: tb_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tb_user_id_seq', 3, true);


--
-- Name: tb_block tb_block_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_block
    ADD CONSTRAINT tb_block_pkey PRIMARY KEY (id);


--
-- Name: tb_booking tb_booking_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_booking
    ADD CONSTRAINT tb_booking_pkey PRIMARY KEY (id);


--
-- Name: tb_client tb_client_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_client
    ADD CONSTRAINT tb_client_pkey PRIMARY KEY (id);


--
-- Name: tb_property tb_property_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_property
    ADD CONSTRAINT tb_property_pkey PRIMARY KEY (id);


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
-- Name: tb_block fk4gugp0cv7cmp714pxb51hxlk5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_block
    ADD CONSTRAINT fk4gugp0cv7cmp714pxb51hxlk5 FOREIGN KEY (property_id) REFERENCES public.tb_property(id);


--
-- Name: tb_client fk667wn59tr8opnrjj5ewa7bx19; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_client
    ADD CONSTRAINT fk667wn59tr8opnrjj5ewa7bx19 FOREIGN KEY (owner_id) REFERENCES public.tb_user(id);


--
-- Name: tb_booking fkcd5s5p7l6xafh7xf8wgx51irh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_booking
    ADD CONSTRAINT fkcd5s5p7l6xafh7xf8wgx51irh FOREIGN KEY (owner_id) REFERENCES public.tb_user(id);


--
-- Name: tb_property fkdp5kocpdj0epjai29uruewg3m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_property
    ADD CONSTRAINT fkdp5kocpdj0epjai29uruewg3m FOREIGN KEY (owner_id) REFERENCES public.tb_user(id);


--
-- Name: tb_block fkjj2q2un4wjxvquhxrjgyngljx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_block
    ADD CONSTRAINT fkjj2q2un4wjxvquhxrjgyngljx FOREIGN KEY (owner_id) REFERENCES public.tb_user(id);


--
-- Name: tb_booking fkr4m7qudfn905b9wpvndk1523j; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_booking
    ADD CONSTRAINT fkr4m7qudfn905b9wpvndk1523j FOREIGN KEY (client_id) REFERENCES public.tb_client(id);


--
-- Name: tb_booking fksn58voqsgs4d79u0bb54kgot6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tb_booking
    ADD CONSTRAINT fksn58voqsgs4d79u0bb54kgot6 FOREIGN KEY (property_id) REFERENCES public.tb_property(id);


--
-- PostgreSQL database dump complete
--

