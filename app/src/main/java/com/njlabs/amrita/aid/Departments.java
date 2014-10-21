package com.njlabs.amrita.aid;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

public class Departments extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String department = extras.getString("department");
        setContentView(R.layout.activity_departments);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(department);
        TextView item = (TextView) this.findViewById(R.id.DepartmenttextView);
        if (department.equals("Aerospace")) {
            item.setText(Html.fromHtml("<p><b>The Department of Aerospace Engineering</b> (AE) started in the year of <b>2007</b>, in order to meet the needs and the trends of the current requirements in the aerospace industries. Presently, the Department offers <b>B.Tech programme in Aerospace Engineering</b>.</p><p>The prescribed courses of study and syllabi, cover areas like, design, structures, propulsion, aerodynamics and systems. The department is headed by an eminent professor, <b>Dr. J. Chandrasekhar</b>, having more than 35 years of experience in the department of Aerospace engineering at <b>IIT-Bombay</b>.</p><p>The onerous task of setting up state-of-the-art laboratories in various fields of study, has become doubly challenging for the Department, which is in its early stages of inception. But, based on its vast research and teaching experience (from reputed institutions in India and abroad), the faculty of the department, has taken up the challenge. The laboratories and workshops, detailed in the curriculum, have been framed to take up the challenges in Industry and focus themselves on research and development. </p><p>The recent purchase of a <b>MiG-23 aircraft</b> from the Indian Air Force is also intended for providing students a greater insight into flight mechanisms. This department also setting up a <b>low speed (up to 50m/s) wind tunnel</b> (2' x 2' - test section) for aerodynamic studies.</p><p>A highly <b>sophisticated CAD/CAM Centre</b> has been set up in the School of Engineering to encourage students interested in developing their software and programming skills. The student has access to the latest versions of many analysis and design packages.</p><p><b>Tie-ups</b> with the pioneering institutions like <b>National Aerospace Laboratories</b> (NAL), <b>Indian Space Research Organizations</b> (ISRO) etc., have served as eye-openers on the complexities of real life problems, both for the students and faculty members.</p>"));
        } else if (department.equals("Civil")) {
            item.setText(Html.fromHtml("<p>The <b>undergraduate programme in Civil Engineering</b>, has been offered at the Coimbatore campus of the Amrita Vishwa Vidyapeetham since <b>2008</b>. The academic activities of the Department lays emphasis on deep understanding of fundamental concepts, development of creative ability to handle the challenges of Civil Engineering, and the analytical ability to solve problems which are interdisciplinary in nature.</p><p>The faculty includes doctorates and post graduates in Civil Engineering from prestigious Institutions like <b>IITs</b>., <b>IISc</b> and <b>NITs</b>. The research programmes in the department focuses on areas like development of new building materials, concrete admixtures, high performance concrete, wastewater treatment and soft computing in Civil Engineering. The Faculty members encourage the students to get involved in the research activities of the department and thereby provide the best possible introduction to hands-on, practical engineering problems.</p><p>The department has state-of-the-art laboratories in the areas of <b>Survey & Geoinformatics</b>, <b>Construction Engineering & Material Evaluation</b>, <b>Geo-technical & Transportation Engineering</b> and <b>Environmental & Water Resources Engineering</b>.</p>"));
        } else if (department.equals("Chemical")) {
            item.setText(Html.fromHtml("<p>India is transforming and repositioning herself globally in her infrastructure, industries, environment, energy and economy. <b>Chemical engineering</b> is one of the disciplines leading and shaping this transformation at the forefront.</p><p>Chemical engineers play a vital role in such diverse industries such as petroleum and petrochemicals, energy, power generation including ultra mega power plants, coal-bed methane, combined gas cycle plants and nuclear power, defense, mining and minerals including steel and aluminum, pharmaceuticals and biotechnology, nanotechnology, cement, fertilizers, textiles, leather, food and agro-based technologies, polymers, and environmental engineering. Chemical engineering is the broadest branch of engineering, training professionals to design, manufacture, operate and control processes of such diversity.</p><p><b>Indian biotech</b> and <b>defense industries</b> are <b>highly research-driven</b> and have among the highest demands for chemical engineers.</p><p>In this context, the demand for qualified professional chemical engineers to propel the growth of the industry and shape the contours of Indian economy is on the rise. The <b>undergraduate(B. Tech) program</b> offered in <b>Chemical Engineering</b> at <b>Amrita University</b> has a curriculum of <b>global standards</b> emphasizing the fundamentals, breadth of the discipline, and the necessary flexibility in being able to apply the fundamentals in widely varying contexts.</p><p>It simultaneously trains the students in specialized elective streams such as <em>materials science, nanotechnology, pharmaceuticals and petrochemicals, and petroleum refineries</em>. With <b>45%</b> of the faculty having a <b>PhD degree</b>, and another<b> 25%</b> actively pursuing their <b>PhD</b>, the students get to learn from well qualified and experienced staff.</p><p>The students learn hands-on engineering skills in excellent labs, workshops and on computers. They also use the excellent library facilities and computing facilities in the campus, and have access to the state-of-the-art instrumentation equipment at <b>Amrita Center for Nanosciences</b>. Amrita School of Engineering and the department have excellent placement records for the graduating students. Nearly <b>25%</b> of our students <b>go abroad</b> every year for higher studies. We have a hardworking and enterprising student group. Our students have been placed in top notch companies like <b>General Electric, Schneider India, Akzo Nobel, Apollo tyres </b>etc.</p>"));
        } else if (department.equals("Computer Science")) {
            item.setText(Html.fromHtml("<p>Ever since its inception on <b>7th October 1996</b>, the <b>Department of Computer Science and Engineering</b> at Amrita Vishwa Vidyapeetham has been progressing towards excellence in the field of teaching and research.</p><p>With a team of dedicated, experienced and qualified faculty members, the department has witnessed tremendous growth in academics and research. Major research areas include <em>Image Processing, Multimedia Mining, Evolutionary Computing, Network Security and Wireless Networks</em>. The department has witnessed constant flow of some of the brightest students and researchers and our students currently hold successful positions in both academia and industry. The department is progressing towards setting up of <b>research laboratories and R & D centers</b>.</p><p>The department offers <b>B.Tech in Computer Science and Engineering</b>, <b>Master of Computer Applications (MCA)</b> and <b>M.Tech in Computer Vision and Image Processing</b>. Regular interaction with software companies has helped the department in maintaining its syllabus abreast with technology and industrial standards. The rigorous learning environment has helped make students job-ready.</p><p>The department has a well-equipped hardware lab. Students of all branches of engineering are given training on assembling computers, interfacing with peripheral devices and troubleshooting. <b>ASCII</b> (Association of Students of Computer Science for Information Interchange) is the technical association of the department which organizes an annual inter-departmental technical contest -Cerebral Wars. Our students have been recruited by top notch IT firms in the country and abroad. Our students regularly participate in exchange programs of European Universities. Year on year, our students have produced good quality projects. Both students and faculty actively promote the use and development of Free and Open Source Software.</p><p>The department is keen in providing a holistic education to students. Students and faculty members have a b sense of social responsibility. They regularly participate in <b>NSS</b> and other <b>community service activities</b>. </p> "));
        } else if (department.equals("Electrical")) {
            item.setText(Html.fromHtml("<p>The department, established in <b>1994</b> has sufficient number of academic and support staff, committed to research and teaching, and well equipped laboratories and library, meeting the requirements of undergraduates, post graduates and research students.</p><p>Graduates and postgraduates of Electrical and Electronics Engineering will be able to take up challenging jobs in a wide range of industries and engage themselves in research and development.</p><p>The department has Power electronics, Electric Machines and Control Systems, Electrical Measurements and Embedded Systems Laboratories in addition to Electrical Workshop. Each laboratory is equipped with instruments and equipments for teaching, learning and research.</p><p>Under the <b>MoU</b> signed with <b>Uppsala University</b>, Sweden, Faculty member is doing research leading to <b>Ph.D</b> degree under twinning programme. In addition faculty members and students are doing research in various <b>European Universities under EURECA Programme</b>.</p>"));
        } else if (department.equals("Electronics")) {
            item.setText(Html.fromHtml("<p><b>Department of Electronics and Communications Engineering</b> aims at training students in the areas of Electronics like Solid state circuits, VLSI, Electronic Controls and Communications Engineering including , Multiple access technology and Microwave Engineering.</p><p>A team of experienced faculty in <b>Analog and Digital Communications</b> are conducting research in various aspects of Communications.</p><p>A<b> VLSI Design group </b>is working on latest device developments and circuit design algorithms at various levels. A <b>Biomedical Engineering group</b> is innovating on telemedicine, signal processing and imaging techniques. <b>Process control and instrumentation group</b> are at developing latest control schemes of industrial processes.</p><p>There are facilities to develop<b> microprocessor/ microcontroller based sensor networks</b> and equipments through guidance from experienced professors and laboratory setup. There is a core signal processing group to assist students to learn communication related applications.</p><p>There are special electives like <b>soft-computing</b>, <b>support vector machines</b> and in other areas of emergence to cope up with the latest trends in application areas. One specialty with the department is that students learn published material from journals and generate publications of International quality. The fact that a sizeable lot of our alumni secured post graduate and research degrees from institutions of repute abroad, vouches the motivation given by our faculty members</p>"));
        } else if (department.equals("Mechanical")) {
            item.setText(Html.fromHtml("<p>The <b>department of Mechanical Engineering</b> was started in the year <b>1994</b>. The department offers <b>B.Tech program in Mechanical Engineering</b>, <b>M.Tech programs in Engineering Design, Manufacturing Engineering, Automotive Engineering</b> and <b>Automotive Systems</b> and <b>Ph.D program</b>.</p><p>At present the department intake capacity is <b>180 students per year</b> in the<b> B.Tech level</b> and 25 students each in the M.Tech programs. The department has <b>competent and committed faculty members</b> drawn from industry, practicing professionals and academicians to enhance the delivery of academic programs. There are about <b>44 teaching faculty</b> in the department. Most of them have <b>at least 15 years of experience</b>.</p><p>The department has evolved a comprehensive <b>student-centric learning approach</b>, designed to add significant value to the learner's understanding in an integrated manner through workshops, lab sessions, assignments, training, seminars, projects and independent study.</p><p>The hands-on training offered our <b>CAD/CAM training</b> in latest design software brings in a formal method of familiarizing with the industrial practices helps the students to apply their class room knowledge to live industrial situations. The department is equipped with a <b>library</b>, <b>computer centre</b> with latest design and analysis software and a <b>seminar hall</b>. <b>The laboratories and computing facilities are made available to the students and staff till late night on all working days.</b></p>"));
        } else if (department.equals("Humanities")) {
            item.setText(Html.fromHtml("<p><b>The Department of English</b> has been functioning as a <b>service department</b> under the ASE from the inception of Amrita University in the year <b>2003-04</b>. Since then, the department has been offering <b>English Language Courses</b> to the students of Engineering & Technology under ASE, and students of Journalism from Amrita School of Journalism.</p><p> These courses have the objectives of <b>improving the communication skills</b> as well as the <b>critical thinking skills</b> of our students. It has also been aiming at <b>inculcating our traditional values and lofty thoughts</b>.</p><p>Apart from the academic activities, the department is also actively involved in activities like the <b>publication of College Magazine - AMRITADHWANI</b>, <b>News letter. AMRITARPAN</b> and also in the activities of the <b>Literary club - SRISHTI</b>. So far eight magazines have been brought out and the magazine has bagged II Prize (twice) and I Prize (twice) among the College Magazines in Tamil Nadu.</p><p><b>Srishti, the literary club</b> has provided a <b>platform for the students to exhibit their literary talents</b> in <b>five languages</b> including English. Through these activities the club has been instrumental in creating interest among students to appreciate <b>good literature of different genres & languages</b>.</p>"));
        } else if (department.equals("Mathematics")) {
            item.setText(Html.fromHtml("<p>The Department started functioning with the inception of Amrita Institute of Technology in <b>1994</b>. The Department has a dedicated team of faculty members which includes a good number of <b>Doctorate Degree holders</b> with rich experience in teaching and research. It has attracted <b>eminent retired professors from premier institutions like IIT</b> who are the backbones in taking the Department forward on the progressive lines. </p><p>Initially, the focus of the Department was to provide a<b> b foundation in Engineering Mathematics</b> to the students of Undergraduate Engineering Programs. Subsequently, apart from offering Elective Courses, with the introduction of Postgraduate Programs and Research Programs in all the Departments, the Faculty got involved in handling in specialized and emerging areas like <em>Number Theory, Cryptology, Operations Research, VLSI Algorithms and Patter Recognition</em> etc. The department offers advanced courses for various Ph.D programs of Amrita School of Engineering. It also offers <b>Ph.D. Program for the faculty members</b> of the Department of Mathematics of various campuses of the University.</p>"));
        } else if (department.equals("Sciences")) {
            item.setText(Html.fromHtml("<p>At the time of start of Amrita Vishwa Vidayapeetham as a deemed university, Ettimadai campus had two small science departments namely <b>physics and chemistry</b>. </p><p>In <b>2006</b> it was felt that for better coordination, operational convenience and to nurture interdisplinary interaction, these two departments should be merged as one single department designated as <b>Department of Sciences</b>.</p><p>This amalgamated department <b>started functioning in July 2006</b>. The department is actively involved in teaching of basic physics and chemistry courses and also a number of elective courses in the B.Tech programme to cater to the student need and interest. </p><p>The department has research interest in the area of <em>Theoretical Nuclear Physics, Quantum Optics, Nanotechnology, Thin Films, Laser Materials Processing, Development of Innovative Glucose Biosensor,</em> and <em>Biodiesel from non-edible oils under Alternate Energy development programme, Organometallics, Porphyrin, Cobaloximes, Corrosion Science, Industrial Electrochemical Processes, cancer related chemistry and science education and popularization</em>.</p><p>The department has <b>funded projects from various agencies</b> like <b>DST, CSIR, DRDO, ISRO</b> etc. Faculty also collaborates with other Engineering disciplines for interdisciplinary research initiatives. In addition Department is active in <b>conducting various activities like seminars,quizzes,etc</b> to nurture the interest of student community in <b>basic science and its relevance to technology.</b></p>"));
        } else {
            item.setText(Html.fromHtml("<br><h2>Error !</h2><p> Please Report </p>"));
        }

    }
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        finish(); //go back to the previous Activity
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}
