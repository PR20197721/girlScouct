#!/bin/bash

#export AEM56=54.84.115.158
#export PORT=[4502/4503]
#export COUNCIL_DAM=[council_dam]
#export PASSWORD=[aem_6_1_password]
#./vlt rcp -r -u -n "http://proddatamigrate:v3swezaz@$AEM56:$PORT/crx/-/jcr:root/content/dam/$COUNCIL_DAM" "http://admin:$PASSWORD@localhost:$PORT/crx/-/jcr:root/content/dam/$COUNCIL_DAM" 2>&1 | tee vlt.log


export PORT=4503
#	SOURCE
export SOURCE_HOST=52.86.150.105
export SOURCE_USER=admin
export SOURCE_PASSWORD=
export SOURCE_CRX=http://$SOURCE_USER:$SOURCE_PASSWORD@$SOURCE_HOST:$PORT/crx/-

#	DESTINATION
export DESTINATION_HOST=52.72.160.170
export DESTINATION_USER=admin
export DESTINATION_PASSWORD=cH*t3uzEsT
export DESTINATION_CRX=http://$DESTINATION_USER:$DESTINATION_PASSWORD@$DESTINATION_HOST:$PORT/crx/-

synchronize(){
	export CONTENT_PATH=jcr:root/content/$1
	export DESIGNS_PATH=jcr:root/etc/designs/$2
	export SCAFFOLDING_PATH=jcr:root/etc/scaffolding/$3
	export TAGS_PATH=jcr:root/etc/tags/$4
	export DAM_PATH=jcr:root/content/dam/$5

	echo 'Synchronizing content:'
	./vlt rcp -r -u -n "$SOURCE_CRX/$CONTENT_PATH" "$DESTINATION_CRX/$CONTENT_PATH"  2>&1 | tee vlt.log
	echo 'Synchronizing designs:'
	./vlt rcp -r -u -n "$SOURCE_CRX/$DESIGNS_PATH" "$DESTINATION_CRX/$DESIGNS_PATH"  2>&1 | tee vlt.log
	echo 'Synchronizing scaffolding:'
	./vlt rcp -r -u -n "$SOURCE_CRX/$SCAFFOLDING_PATH" "$DESTINATION_CRX/$SCAFFOLDING_PATH"  2>&1 | tee vlt.log
	echo 'Synchronizing tags:'
	./vlt rcp -r -u -n "$SOURCE_CRX/$TAGS_PATH" "$DESTINATION_CRX/$TAGS_PATH"  2>&1 | tee vlt.log
	echo 'Synchronizing dam:'
	./vlt rcp -r -u -n "$SOURCE_CRX/$DAM_PATH" "$DESTINATION_CRX/$DAM_PATH"  2>&1 | tee vlt.log
}

echo 'Synchronizing council: [www.girlscouts-gateway.org]'
synchronize gateway girlscouts-gateway gateway gateway gateway

echo 'Synchronizing council: [www.girlscoutcsa.org]'
synchronize girlscoutcsa girlscouts-girlscoutcsa girlscoutcsa southern-appalachian 

echo 'Synchronizing council: [www.gsnetx.org]'
synchronize gsnetx girlscouts-gsnetx gsnetx gsnetx NE_Texas

echo 'Synchronizing council: [www.nccoastalpines.org]'
synchronize girlscoutsnccp girlcouts-nccp girlscoutsnccp girlscoutsnccp nc-coastal-pines-images-

echo 'Synchronizing council: [www.gswcf.org]'
synchronize gswcf girlscouts-gswcf gswcf gswcf wcf-images

echo 'Synchronizing council: [www.gssem.org]'
synchronize gssem girlscouts-gssem gssem gssem gssem

echo 'Synchronizing council: [www.gssjc.org]'
synchronize gssjc girlscouts-gssjc gssjc gssjc gssjc

echo 'Synchronizing council: [www.gsctx.org]'
synchronize gsctx girlscouts-gsctx gsctx gsctx girlscouts-gsctx

echo 'Synchronizing council: [www.girlscoutsaz.org]'
synchronize girlscoutsaz girlscouts-girlscoutsaz girlscoutsaz girlscoutsaz girlscoutsaz

echo 'Synchronizing council: [www.girlscoutsnv.org]'
synchronize girlscoutsnv girlscouts-gssnv girlscoutsnv girlscoutsnv gssnv

echo 'Synchronizing council: [www.kansasgirlscouts.org]'
synchronize kansasgirlscouts girlscouts-kansasgirlscouts kansasgirlscouts kansasgirlscouts kansasgirlscouts

echo 'Synchronizing council: [www.gswestok.org]'
synchronize gswestok girlscouts-gswestok gswestok gswestok gswestok

echo 'Synchronizing council: [www.gskentuckiana.org]'
synchronize gskentuckiana girlscouts-gskentuckiana gskentuckiana gskentuckiana gskentuckiana

echo 'Synchronizing council: [www.gswo.org]'
synchronize gswo girlscouts-gswo gswo gswo gswo

echo 'Synchronizing council: [www.gseok.org]'
synchronize gseok girlscouts-gseok gseok gseok girlscouts-gseok

echo 'Synchronizing council: [www.girlscoutsosw.org]'
synchronize girlscoutsosw girlscouts-girlscoutsosw girlscoutsosw girlscoutsosw oregon-sw-washington

echo 'Synchronizing council: [www.gssn.org]'
synchronize gssn girlscouts-gssn gssn gssn gssn

echo 'Synchronizing council: [www.gsneo.org]'
synchronize gsneo girlscouts-gsneo gsneo gsneo gsneo

echo 'Synchronizing council: [www.usagso.org]'
synchronize usagso girlscouts-usagso usagso usagso usagso

echo 'Synchronizing council: [www.girlscoutsofcolorado.org]'
synchronize girlscoutsofcolorado girlscouts-girlscoutsofcolorado girlscoutsofcolorado girlscoutsofcolorado girlscoutsofcolorado

echo 'Synchronizing council: [www.girlscoutstoday.org]'
synchronize girlscoutstoday girlscouts-girlscoutstoday girlscoutstoday girlscoutstoday girlscoutstoday

echo 'Synchronizing council: [www.gsbadgerland.org]'
synchronize gsbadgerland girlscouts-gsbadgerland gsbadgerland gsbadgerland gsbadgerland

echo 'Synchronizing council: [www.gscnc.org]'
synchronize gscnc girlscouts-gscnc gscnc gscnc girlscouts-gscnc

echo 'Synchronizing council: [www.girlscoutsoc.org]'
synchronize girlscoutsoc girlscouts-girlscoutsoc girlscoutsoc girlscoutsoc girlscoutsoc

echo 'Synchronizing council: [www.gscsnj.org]'
synchronize gscsnj girlscouts-gscsnj gscsnj gscsnj gscsnj

echo 'Synchronizing council: [www.gskentucky.org]'
synchronize gskentucky girlscouts-gskentucky gskentucky gskentucky girlscouts-gskentucky

echo 'Synchronizing council: [www.girlscoutsgcnwi.org]'
synchronize girlscoutsgcnwi girlscouts-girlscoutsgcnwi girlscoutsgcnwi girlscoutsgcnwi girlscouts-girlscoutsgcnwi

echo 'Synchronizing council: [www.sdgirlscouts.org]'
synchronize sdgirlscouts girlscouts-sdgirlscouts sdgirlscouts sdgirlscouts girlscouts-sdgirlscouts

echo 'Synchronizing council: [www.gsutah.org]'
synchronize gsutah girlscouts-gsutah gsutah gsutah girlscouts-gsutah

echo 'Synchronizing council: [www.gswpa.org]'
synchronize gswpa girlscouts-gswpa gswpa gswpa girlscouts-gswpa

echo 'Synchronizing council: [www.gsnorcal.org]'
synchronize gsnorcal girlscouts-gsnorcal gsnorcal gsnorcal girlscouts-gsnorcal

echo 'Synchronizing council: [www.gshpa.org]'
synchronize gshpa girlscouts-gshpa gshpa gshpa girlscouts-gshpa

echo 'Synchronizing council: [www.girlscoutsatl.org]'
synchronize girlscoutsatl girlscouts-girlscoutsatl girlscoutsatl girlscoutsatl girlscouts-girlscoutsatl

echo 'Synchronizing council: [www.gshnj.org]'
synchronize gshnj girlscouts-gshnj gshnj gshnj girlscouts-gshnj

echo 'Synchronizing council: [www.girlscoutsiowa.org]'
synchronize girlscoutsiowa girlscouts-girlscoutsiowa girlscoutsiowa girlscoutsiowa girlscouts-girlscoutsiowa

echo 'Synchronizing council: [www.gscwm.org]'
synchronize gscwm girlscouts-gscwm gscwm gscwm girlscouts-gscwm

echo 'Synchronizing council: [www.bdgsc.org]'
synchronize bdgsc girlscouts-bdgsc bdgsc bdgsc girlscouts-bdgsc

echo 'Synchronizing council: [www.girlscoutshh.org]'
synchronize girlscoutshh girlscouts-girlscoutshh girlscoutshh girlscoutshh girlscouts-girlscoutshh

echo 'Synchronizing council: [www.gshg.org]'
synchronize gshg girlscouts-gshg gshg gshg girlscouts-gshg

echo 'Synchronizing council: [www.girlscoutssoaz.org]'
synchronize girlscoutssoaz girlscouts-girlscoutssoaz girlscoutssoaz girlscoutssoaz girlscouts-girlscoutssoaz

echo 'Synchronizing council: [www.citrus-gs.org]'
synchronize citrus-gs girlscouts-citrus-gs citrus-gs citrus-gs girlscouts-citrus-gs

echo 'Synchronizing council: [www.gsgst.org]'
synchronize gsgst girlscouts-gsgst gsgst gsgst girlscouts-gsgst

echo 'Synchronizing council: [www.gsnypenn.org]'
synchronize gsnypenn girlscouts-gsnypenn gsnypenn gsnypenn girlscouts-gsnypenn

echo 'Synchronizing council: [www.girlscoutsww.org]'
synchronize girlscoutsww girlscouts-girlscoutsww girlscoutsww girlscoutsww girlscouts-girlscoutsww

echo 'Synchronizing council: [www.gs-top.org]'
synchronize gs-top girlscouts-gs-top gs-top gs-top girlscouts-gs-top

echo 'Synchronizing council: [www.girlscouts-swtx.org]'
synchronize girlscouts-swtx girlscouts-girlscouts-swtx girlscouts-swtx girlscouts-swtx girlscouts-girlscouts-swtx

echo 'Synchronizing council: [www.girlscoutshcc.org]'
synchronize girlscoutshcc girlscouts-girlscoutshcc girlscoutshcc girlscoutshcc girlscouts-girlscoutshcc

echo 'Synchronizing council: [www.girlscoutsesc.org]'
synchronize girlscoutsesc girlscouts-girlscoutsesc girlscoutsesc girlscoutsesc girlscouts-girlscoutsesc

echo 'Synchronizing council: [www.gsle.org]'
synchronize gsle girlscouts-gsle gsle gsle girlscouts-gsle

echo 'Synchronizing council: [www.gsema.org]'
synchronize girlscoutseasternmass girlscouts-girlscoutseasternmass girlscoutseasternmass girlscoutseasternmass girlscouts-girlscoutseasternmass

echo 'Synchronizing council: [www.gsmw.org]'
synchronize gsmw girlscouts-gsmw gsmw gsmw girlscouts-gsmw

echo 'Synchronizing council: [www.jerseyshoregirlscouts.org]'
synchronize jerseyshoregirlscouts girlscouts-jerseyshoregirlscouts jerseyshoregirlscouts jerseyshoregirlscouts girlscouts-jerseyshoregirlscouts

echo 'Synchronizing council: [www.girlscoutsni.org]'
synchronize girlscoutsni girlscouts-girlscoutsni girlscoutsni girlscoutsni girlscouts-girlscoutsni

echo 'Synchronizing council: [www.gssc-mm.org]'
synchronize gssc-mm girlscouts-gssc-mm gssc-mm gssc-mm girlscouts-gssc-mm

echo 'Synchronizing council: [www.girlscoutsgwm.org]'
synchronize girlscoutsgwm girlscouts-girlscoutsgwm girlscoutsgwm girlscoutsgwm girlscouts-girlscoutsgwm

echo 'Synchronizing council: [www.girlscoutsofmaine.org]'
synchronize girlscoutsofmaine girlscouts-girlscoutsofmaine girlscoutsofmaine girlscoutsofmaine girlscouts-girlscoutsofmaine

echo 'Synchronizing council: [www.nmgirlscouts.org]'
synchronize nmgirlscouts girlscouts-nmgirlscouts nmgirlscouts nmgirlscouts girlscouts-nmgirlscouts

echo 'Synchronizing council: [www.girlscouts-gssi.org]'
synchronize girlscouts-gssi girlscouts-girlscouts-gssi girlscouts-gssi girlscouts-gssi girlscouts-girlscouts-gssi

echo 'Synchronizing council: [www.gsewni.org]'
synchronize gsewni girlscouts-gsewni gsewni gsewni girlscouts-gsewni

echo 'Synchronizing council: [www.gsoh.org]'
synchronize gsoh girlscouts-gsoh gsoh gsoh girlscouts-gsoh

echo 'Synchronizing council: [www.gswise.org]'
synchronize gswise girlscouts-gswise gswise gswise girlscouts-gswise

echo 'Synchronizing council: [www.gsnc.org]'
synchronize gsnc girlscouts-gsnc gsnc gsnc girlscouts-gsnc

echo 'Synchronizing council: [www.gssef.org]'
synchronize gssef girlscouts-gssef gssef gssef girlscouts-gssef

echo 'Synchronizing council: [www.gswny.org]'
synchronize gswny girlscouts-gswny gswny gswny girlscouts-gswny

echo 'Synchronizing council: [www.gshawaii.org]'
synchronize girlscouts-hawaii girlscouts-girlscouts-hawaii girlscouts-hawaii girlscouts-hawaii girlscouts-girlscouts-hawaii

echo 'Synchronizing council: [www.girlscoutshs.org]'
synchronize girlscoutshs girlscouts-girlscoutshs girlscoutshs girlscoutshs girlscouts-girlscoutshs

echo 'Synchronizing council: [www.gssne.org]'
synchronize gssne girlscouts-gssne gssne gssne girlscouts-gssne

echo 'Synchronizing council: [www.girlscoutsnebraska.org]'
synchronize girlscoutsnebraska girlscouts-girlscoutsnebraska girlscoutsnebraska girlscoutsnebraska girlscouts-girlscoutsnebraska

echo 'Synchronizing council: [www.girlscoutsnorthernindiana-michiana.org]'
synchronize girlscoutsnorthernindiana-michiana girlscouts-girlscoutsnorthernindiana-michiana girlscoutsnorthernindiana-michiana girlscoutsnorthernindiana-michiana girlscouts-girlscoutsnorthernindiana-michiana

echo 'Synchronizing council: [www.gsvsc.org]'
synchronize gsvsc girlscouts-gsvsc gsvsc gsvsc girlscouts-gsvsc

echo 'Synchronizing council: [www.gsgcf.org]'
synchronize gsgcf girlscouts-gsgcf gsgcf gsgcf girlscouts-gsgcf

echo 'Synchronizing council: [www.girlscoutsindiana.org]'
synchronize girlscoutsindiana girlscouts-girlscoutsindiana girlscoutsindiana girlscoutsindiana girlscouts-girlscoutsindiana

echo 'Synchronizing council: [www.gscm.org]'
synchronize gscm girlscouts-gscm gscm gscm girlscouts-gscm

echo 'Synchronizing council: [www.gslpg.org]'
synchronize gslpg girlscouts-gslpg gslpg gslpg girlscouts-gslpg

echo 'Synchronizing council: [www.getyourgirlpower.org]'
synchronize getyourgirlpower girlscouts-getyourgirlpower getyourgirlpower getyourgirlpower girlscouts-getyourgirlpower

echo 'Synchronizing council: [www.girlscoutsccc.org]'
synchronize girlscoutsccc girlscouts-girlscoutsccc girlscoutsccc girlscoutsccc girlscouts-girlscoutsccc

echo 'Synchronizing council: [www.gsnnj.org]'
synchronize gsnnj girlscouts-gsnnj gsnnj gsnnj girlscouts-gsnnj

echo 'Synchronizing council: [www.juliettegordonlowbirthplace.org]'
synchronize juliettegordonlowbirthplace girlscouts-juliettegordonlowbirthplace juliettegordonlowbirthplace juliettegordonlowbirthplace girlscouts-juliettegordonlowbirthplace

echo 'Synchronizing council: [www.comgirlscouts.org]'
synchronize comgirlscouts girlscouts-comgirlscouts comgirlscouts comgirlscouts girlscouts-comgirlscouts

echo 'Synchronizing council: [www.gscfp.org]'
synchronize gscfp girlscouts-gscfp gscfp gscfp girlscouts-gscfp

echo 'Synchronizing council: [www.girlscoutsem.org]'
synchronize girlscoutsem girlscouts-girlscoutsem girlscoutsem girlscoutsem girlscouts-girlscoutsem

echo 'Synchronizing council: [www.girlscouts-ssc.org]'
synchronize girlscouts-ssc girlscouts-girlscouts-ssc girlscouts-ssc girlscouts-ssc girlscouts-girlscouts-ssc

echo 'Synchronizing council: [www.gsccc.org]'
synchronize gsccc girlscouts-gsccc gsccc gsccc girlscouts-gsccc

echo 'Synchronizing council: [www.girlscoutslp.org]'
synchronize gslakesandpines girlscouts-gslakesandpines gslakesandpines gslakesandpines girlscouts-gslakesandpines

echo 'Synchronizing council: [www.girlscoutsmoheartland.org]'
synchronize girlscoutsmoheartland girlscouts-girlscoutsmoheartland girlscoutsmoheartland girlscoutsmoheartland girlscouts-girlscoutsmoheartland

echo 'Synchronizing council: [www.gsnwgl.org]'
synchronize gsnwgl girlscouts-gsnwgl gsnwgl gsnwgl girlscouts-gsnwgl

echo 'Synchronizing council: [www.gsdakotahorizons.org]'
synchronize gsdakotahorizons girlscouts-gsdakotahorizons gsdakotahorizons gsdakotahorizons girlscouts-gsdakotahorizons

echo 'Synchronizing council: [www.girlscoutsp2p.org]'
synchronize girlscoutsp2p girlscouts-girlscoutsp2p girlscoutsp2p girlscoutsp2p girlscouts-girlscoutsp2p

echo 'Synchronizing council: [www.girlscoutsnca.org]'
synchronize girlscoutsnca girlscouts-girlscoutsnca girlscoutsnca girlscoutsnca girlscouts-girlscoutsnca

echo 'Synchronizing council: [www.girlscoutsfl.org]'
synchronize girlscoutsfl girlscouts-girlscoutsfl girlscoutsfl girlscoutsfl girlscouts-girlscoutsfl

echo 'Synchronizing council: [www.girlscoutsdiamonds.org]'
synchronize girlscoutsdiamonds girlscouts-girlscoutsdiamonds girlscoutsdiamonds girlscoutsdiamonds girlscouts-girlscoutsdiamonds

echo 'Synchronizing council: [www.gssgc.org]'
synchronize gssgc girlscouts-gssgc gssgc gssgc girlscouts-gssgc

echo 'Synchronizing council: [www.girlscoutssa.org]'
synchronize girlscoutssa girlscouts-girlscoutssa girlscoutssa girlscoutssa girlscouts-girlscoutssa

echo 'Synchronizing council: [www.girlscoutsccs.org]'
synchronize girlscoutsccs girlscouts-girlscoutsccs girlscoutsccs girlscoutsccs girlscouts-girlscoutsccs

echo 'Synchronizing council: [www.hngirlscouts.org]'
synchronize hngirlscouts girlscouts-hngirlscouts hngirlscouts hngirlscouts girlscouts-hngirlscouts

echo 'Synchronizing council: [www.gscb.org]'
synchronize gscb girlscouts-gscb gscb gscb girlscouts-gscb

echo 'Synchronizing council: [www.girlscoutsla.org]'
synchronize girlscoutsla girlscouts-girlscoutsla girlscoutsla girlscoutsla girlscouts-girlscoutsla

echo 'Synchronizing council: [www.gsep.org]'
synchronize gsep girlscouts-gsep gsep gsep girlscouts-gsep

echo 'Synchronizing council: [www.gsofsi.org]'
synchronize gsofsi girlscouts-gsofsi gsofsi gsofsi girlscouts-gsofsi

echo 'Synchronizing council: [www.gsofct.org]'
synchronize gsofct girlscouts-gsofct gsofct gsofct girlscouts-gsofct

echo 'Synchronizing council: [www.gsgms.org]'
synchronize gsgms girlscouts-gsgms gsgms gsgms girlscouts-gsgms

echo 'Synchronizing council: [www.gsdsw.org]'
synchronize gsdsw girlscouts-gsdsw gsdsw gsdsw girlscouts-gsdsw

echo 'Synchronizing council: [www.gsksmo.org]'
synchronize gsksmo girlscouts-gsksmo gsksmo gsksmo girlscouts-gsksmo

echo 'Synchronizing council: [www.gsmanitou.org]'
synchronize gsmanitou girlscouts-gsmanitou gsmanitou gsmanitou girlscouts-gsmanitou

echo 'Synchronizing council: [www.cgspr.org]'
synchronize cgspr girlscouts-cgspr cgspr cgspr girlscouts-cgspr