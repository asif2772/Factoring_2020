package factoring_2020

import bv.auth.InitService
import bv.auth.Role
import bv.auth.User
import factoring.TaxCategory
import factoring.VatCategory
import factoring.SystemPrefix
import groovy.sql.Sql

import javax.sql.DataSource

class BootStrap {

    InitService initService
    DataSource dataSource
    def scheduleJobService
    def init = { servletContext ->
        def isUserRoleExist = Role.findByAuthority("ROLE_USER")
        if(!isUserRoleExist){
            initService.saveUserRole()
        }
//        scheduleJobService.startBootScheduleJob()
//        this.insertUserInfo();
//        this.insertNextGeneratedNumber();
//        this.insertCoreParams();
//
//        this.insertCompanySetup();
//        this.bankRelatedData();
//        this.insertChartClassType();

//        initService.tempUserData()
    }

    def insertCompanySetup(){
        def db = new Sql(dataSource)
        String strPaymentTerms = """INSERT INTO `payment_terms` (`id`, `version`, `alert_repeat_days`, `alert_start_days`, `days_before_due`, `status`, `terms`) VALUES
                                (1, 1, 14, 14, 30, 1, '1 maand'),
                                (2, 0, 14, 7, 14, 1, '2 weken')"""

        db.execute(strPaymentTerms);

        //COMPANY SETUP INITIAL DATA INSERT
        String strCompanySetup = """INSERT INTO company_setup (id, version, language, address_line1, address_line2, amount_decimal_point,
                                    chamber_commerce_no, city, company_date_format, company_full_name, company_short_name, country, date_seperator,
                                    decimal_rounding_type, decimal_seprator, email_address, fax_no, income_tax_reservation, logo, mobile_no,
                                    number_of_booking_period, payment_term_id, percentage_decimal_point, phone_no, postal_address_line1,
                                    postal_city, postal_country, postal_state, postal_zip_code, quantity_decimal_point, report_page_size,
                                    second_email_address, show_glcode_in_report, show_itemcode_in_report, state, tax_category_id, tax_no,
                                    thousand_seperator, unforeseen_expense_reservation, vat_category_id, vat_no, website_address, zip_code)
                                    values (1, 1, 'en', 'test', 'test', 1, 1, 'Amsterdam', 'DDMMYYYY', 'BudgetView', 'BV', 'Netherlands', ',',
                                    2, '.', 'info@budgetview.nl', '9865', 1, 'logo.jpg', '456', 0, 1, 1, '6465', 'test', 'Amsterdam', 'Netherlands',
                                    'Amsterdam', '1216', 1, 'A4', 'admin@budgetview.nl', 'Yes', 'Yes',
                                    'Amsterdam', 1, '123', ',', '0', 1, '123', 'budgetview.nl', '1216')"""

        db.execute(strCompanySetup);

        String strCountries = """INSERT INTO `countries` (`id`, `version`, `iso2`, `iso3`, `name`, `numcode`, `printablename`, `status`) VALUES
                                (1, 0, 'US', 'USA', 'USA', 1, 'United States', 1),
                                (2, 0, 'NL', 'NLD', 'NLD', 31, 'Netherlands', 1)"""

        db.execute(strCountries);


    }

    def insertUserInfo(){

        def db = new Sql(dataSource)

        //Insert Role
        String roleQuery = """INSERT INTO `role` (`id`, `version`, `authority`) VALUES (1, 0, 'ROLE_ADMIN'),(2, 0, 'ROLE_ACCOUNTANT')""";
        db.execute(roleQuery);

        new User(id: 1,username: 'admin',password: 'admin123',email: 'admin@yahoo.com',firstName: 'Head',lastName: 'Admin',enabled: true).save(failOnError: true)
        new User(id: 2,username: 'accountant',password: 'accountant123',email: 'accountant@yahoo.com',firstName: 'Head',lastName: 'Accountant',enabled: true).save(failOnError: true)
        new User(id: 3,username: 'accountant1',password: 'accountant1',email: 'accountant1@yahoo.com',firstName: 'Another',lastName: 'Accountant1',enabled: true).save(failOnError: true)

        String usersRole = """INSERT INTO `user_role` VALUES (1,1),(2,2),(2,3)""";
        db.execute(usersRole);
    }

    def insertNextGeneratedNumber(){
        //new bv.NextGeneratedNumber(id: 1, version: 0, customer: 0, vendor: 0,product:0,journalEntry:0,quickEntry:0,budgetExpense:0,invoiceExpense:0,invoiceIncome:0,invoiceInvestment:0,internalBanking:0,budgetIncome:0,receiptEntry:0).save()
        def db = new Sql(dataSource)
        String NextGeneratedNumberEntry = """insert  into `next_generated_number`(`id`,`version`,`budget_expense`,`budget_income`,`customer`,`internal_banking`,`invoice_expense`,`invoice_income`,`invoice_investment`,`journal_entry`,`product`,`quick_entry`,`receipt_entry`,`vendor`,`recenciliation_entry`,write_off) values (1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)""";
        db.execute(NextGeneratedNumberEntry);
    }

    def bankRelatedData(){
        def db = new Sql(dataSource)
        String bank_account_type = """INSERT INTO `bank_account_type` (`id`, `version`, `description`, `name`, `status`) VALUES
                                            (1, 0, 'Current account', 'Current', 1),
                                            (3, 0, 'Cash', 'Cash', 1),
                                            (4, 0, 'pensioenrekening', 'pensioenrekening', 1)"""

        db.execute(bank_account_type);
    }

    def insertCoreParams() {

        //TAX INITIAL DATA INSERT
        initService.saveTaxInit()

        //VAT INITIAL DATA INSERT
        //new VatCategory(categoryName: 'Test', rate: 25, status: 1, purchasingGlCode:1).save()
        initService.saveVatCatInit()

        //SYSTEM PREFIX DATA INSERT
        initService.saveSysPrefixInit()

        //Reconciliation Booking Type/////
        def db = new Sql(dataSource)
        String reconciliationBookingType = """INSERT INTO `reconciliation_booking_type` (`id`, `version`, `form_type`, `gl_account`, `is_fixed_gl`, `payment_type`, `status`) VALUES
                                            (1, 0, 1, '2300', 1, 'Betaling geboekte factuur of bonnetje', 1),
                                            (2, 0, 2, '2300', 1, 'Nog niet geboekte factuur met BTW', 2),
                                            (4, 0, 4, '1550', 1, 'BTW betaling', 1),
                                            (5, 0, 4, '1410', 1, 'Privé opnames', 1),
                                            (6, 0, 4, '2210', 1, 'Interne overboeking', 1),
                                            (7, 0, 4, '1200', 1, 'Kas opname of storting', 1),
                                            (8, 0, 4, '2220', 1, 'Leningen en afbetalingen', 1),
                                            (9, 0, 4, '', 0, 'Overige betalingen (geen kosten of opbrengsten) Payment', 1)"""
        db.execute(reconciliationBookingType);

//        DebitCreditGlSetup
        String strDebitCreditGlSetup = """INSERT INTO `debit_credit_gl_setup` (`id`, `version`, `creditor_gl_code`,`debitor_gl_code`,`reconcilation_gl_code`, `last_updated`, `chart_group_for_cash`, `for_equity`, `profit_for_year`, `private_withdrawal_code`) VALUES
                                          (1,1,'1600','1300','1200','2015-01-01 12:00:00',22,'0100','0100','1410')"""
        db.execute(strDebitCreditGlSetup);

        //Core Params
        String coreParams = """INSERT INTO `core_params` (`id`, `version`, `params_name`, `type_name`, `var_name`) VALUES
                            (1, 0, 'Status{0}::Actief{1}::Inactief{2}::Verwijderd{-1}', 'select', 'STATUS'),
                            (2, 0, 'Man{''Male''}::Vrouw{''Female''}', 'select', 'GENDER'),
                            (3, 0, 'Eenmalig{''Once''}::Maandelijks{''monthly''}::Twee maandelijks{''two monthly''}::Per kwartaal{''Quarterly''}::Tweemaal per jaar{''twice a year''}::Jaarlijks{''yearly''}', 'select', 'INVOICE_FREQUENCE'),
                            (4, 0, 'Retail{''Retail''}::Whole Sale{''Whole Sale''}', 'select', 'SALES_TYPE'),
                            (5, 0, 'Verkoop{''Sales''}::Inkoop{''Purchase''}::Beide{''Both''}', 'select', 'PRODUCT_TYPE'),
                            (6, 0, 'DDMMYYYY{''DDMMYYYY''}::DDMMYY{''DDMMYY''}::MMDDYYYY{''MMDDYYYY''}::MMDDYY{''MMDDYY''}::YYYYMMDD{''YYYYMMDD''}::YYMMDD{''YYMMDD''}::YYYYDDMM{''YYYYDDMM''}::YYDDMM{''YYDDMM''}', 'select', 'DATEFORMAT'),
                            (7, 0, '1{''1''}::2{''2''}::3{''3''}::4{''4''}', 'select', 'AMOUNT_DECIMAL_POINT'),
                            (8, 0, '1{''1''}::2{''2''}::3{''3''}::4{''4''}', 'select', 'PERCENTAGE_DECIMAL_POINT'),
                            (9, 0, '1{''1''}::2{''2''}::3{''3''}::4{''4''}', 'select', 'QUANTITY_DECIMAL_POINT'),
                            (10, 0, 'Floor Rounging{''Floor Rounding''}::Celing Rounding{''Celing Rounding''}', 'select', 'DECIMAL_ROUNDING'),
                            (11, 0, 'Punt(.){''.''}::Komma(,){'',''}', 'select', 'DECIMAL_SEPARATOR'),
                            (12, 0, 'Punt(.){''.''}::Komma(,){'',''}::Spatie( ){'' ''}', 'select', 'THOUSAND_SEPARATOR'),
                            (13, 0, 'English{''en''}::Nederlands{''du''}', 'select', 'LANGUAGE'),
                            (14, 0, 'Slash(/){''/''}::Punt(.){''.''}::Hyphen(-){''-''}::Dubbele punt(:){'':''}::Komma('',''){'',''}::Spatie( ){'' ''}', 'select', 'DATE_SEPARATOR'),
                            (15, 0, 'Pagina{''letter''}::A4{''A4''}', 'select', 'PAPER_SIZE'),
                            (16, 0, 'Spaarrekening{''Saving Account''}::Betaalrekening{''Cheque Account''}::Krediet rekening{''Credit Account''}::Kas{''Cash Account''}', 'select', 'BANK_ACCOUNT_TYPE'),
                            (17, 0, 'Maandelijks{''1''}::Per kwartaal{''3''}::Per half jaar{''6''}::Jaarlijks{''12''}', 'select', 'BOOKING_PERIOD'),
                            (18, 0, 'Eenmalig{''once''}::Weekelijks{''weekly''}::Elke twee weken{''half_month''}::Maandelijks{''monthly''}::Twee Maandelijks{''two_monthly''}:: Elk kartaal{''three_monthly''}::Half jaarlijks{''six_monthly''}::jaarlijks{''yearly''}', 'select', 'FREQUENCY_INVOICE'),
                            (19, 0, 'User Block{''normal''}::User IP Block{''user_ip_block''}::User IP Range Block{''user_ip_range_block''}::IP Block{''ip_block''}::MAC Block{''mac_block''}::Country Block{''country_block''}', 'select', 'BLOCK_TYPE'),
                            (20, 0, 'Ja{''1''}::Nee{''0''}', 'select', 'AWARE_NOTIFICATION'),
                            (21, 0, 'Ja{''1''}::Nee{''0''}', 'select', 'SECURITY_QUESTION'),
                            (22, 0, 'Retail{''Retail''}::Whole Sale{''Whole Sale''}', 'select', 'PURCHASE_TYPE'),
                            (23, 0, 'Goed{''Good History''}::In Faillisement{''In Liquidation''}::Leveringsstop{''No More Work untill Payment Receive''}', 'select', 'CREDIT_STATUS'),
                            (24, 0, 'Ja{''1''}::Nee{''0''}', 'select', 'YESNO'),
                            (25, 0, 'Account{''Account''}::Marketing{''Marketing''}', 'select', 'CONTACT_DEAL_TYPE'),
                            (26, 0, 'Maandelijks{''monthly''}::Twee maandelijks{''two_monthly''}::Per kwartaal{''Quarterly''}::Tweemaal per jaar{''twice_a_year''}::Jaarlijks{''yearly''}', 'select', 'BUDGET_FREQUENCE'),
                            (27, 0, 'Wisselende leveranciers{''bn''}::Vaste Leverancier{''vn''}::Winkel naam{''sn''}::Type betaling{''rp''}', 'select', 'VENDOR_TYPE'),
                            (28, 0, 'Wisselende klanten{''bn''}::Vaste Klant{''cn''}', 'select', 'CUSTOMER_TYPE')"""

        db.execute(coreParams);
    }

    def insertChartClassType() {

        def db = new Sql(dataSource)
        String chartClassTypeEntry = """INSERT INTO `chart_class_type` (`id`, `version`, `class_type`, `status`, `accountant_name`) VALUES
                                    (1, 0, 'Bezittingen', 1, 'Activa'),
                                    (2, 0, 'Verplichtingen', 1, 'Passiva'),
                                    (3, 0, 'Kapitaal van het bedrijf', 1, 'Eigen vermogen'),
                                    (4, 0, 'Omzet (verkoop)', 1, 'Omzet'),
                                    (5, 0, 'Kostprijs van de omzet', 1, 'Kostprijs van de omzet'),
                                    (6, 0, 'Bedrijfskosten', 1, 'Bedrijfskosten'),
                                    (7, 0, 'Financiële inkomsten en uitgaven', 1, 'Financiële baten en lasten')""";
        db.execute(chartClassTypeEntry);

        String chartClassEntry = """INSERT INTO `chart_class` (`id`, `version`, `chart_class_type_id`, `name`, `status`, `accountant_name`) VALUES
                                (1, 2, 1, 'Immateriële bezittingen', 1, 'Immateriële vaste activa'),
                                (2, 2, 1, 'Materiële bezittingen', 1, 'Materiële vaste activa'),
                                (3, 2, 1, 'Financiële bezittingen', 1, 'Financiële vaste activa'),
                                (4, 2, 1, 'Voorraden', 1, 'Voorraden'),
                                (5, 2, 1, 'Te ontvangen bedragen', 1, 'Vorderingen'),
                                (6, 2, 1, 'Banktegoeden en kas', 1, 'Liquide middelen'),
                                (7, 2, 2, 'Kapitaal vh bedrijf', 1, 'Eigen vermogen'),
                                (8, 2, 2, 'Voorzieningen', 1, 'Voorzieningen'),
                                (9, 2, 2, 'Langlopende schulden', 1, 'Langlopende schulden'),
                                (10, 2, 2, 'Te betalen Bedragen', 1, 'Kortlopende schulden'),
                                (11, 1, 4, 'Verkoop - omzet', 1, 'Omzet'),
                                (12, 1, 5, 'Inkoopwaarde van de omzet', 1, 'Kostprijs van de omzet'),
                                (13, 1, 6, 'Bedrijfskosten', 1, 'Bedrijfskosten'),
                                (14, 1, 7, 'Financierings en rentelasten', 1, 'Financiële baten en lasten')""";
        db.execute(chartClassEntry);

        String chartGroupEntry = """INSERT INTO `chart_group` (`id`, `version`, `chart_class_id`, `name`, `status`, `accountant_name`) VALUES
                                (1, 0, 1, 'Goodwill', 1, 'Goodwill'),
                                (2, 0, 1, 'Octrooien, patenten en rechten', 1, 'Octrooien, patenten en rechten'),
                                (3, 0, 1, 'Research en ontwikkeling', 1, 'Research en ontwikkeling'),
                                (4, 0, 1, 'Overige immateriële bezittingen', 1, 'Overige immateriële activa'),
                                (5, 0, 2, 'Machines en apparaten', 1, 'Machines en apparaten'),
                                (6, 0, 2, 'Computers en computerapparatuur', 1, 'Computers en computerapparatuur'),
                                (7, 0, 2, 'Auto en transportmiddelen', 1, 'Auto en transportmiddelen'),
                                (8, 0, 2, 'Inventaris & Inrichting', 1, 'Inventaris & Inrichting'),
                                (9, 0, 2, 'Gebouwen & grond', 1, 'Gebouwen & grond'),
                                (10, 0, 2, 'Overige materiële bezittingen', 1, 'Overige materiële vaste activa'),
                                (11, 0, 3, 'Waarborgsommen', 1, 'Waarborgsommen'),
                                (12, 0, 3, 'Deelnemingen in andere bedrijven', 1, 'Deelnemingen in andere bedrijven'),
                                (13, 0, 3, 'Strategische langlopende beleggingen', 1, 'Strategische langlopende beleggingen'),
                                (14, 0, 3, 'Verstrekte langlopende leningen (hypotheek ed)', 1, 'Verstrekte langlopende leningen (hypotheek ed)'),
                                (15, 0, 3, 'Overige financiële bezittingen', 1, 'Overige financiële vaste activa'),
                                (16, 0, 4, 'Voorraad', 1, 'Voorraad'),
                                (17, 0, 5, 'Debiteuren (openstaande verkoopfacturen)', 1, 'Handelsdebiteuren'),
                                (18, 0, 5, 'Te ontvangen Belasting en premies sociale verzekeringen', 1, 'Belasting en premies sociale verzekeringen'),
                                (19, 0, 5, 'Waarde beleggingen', 1, 'Beleggingen'),
                                (20, 0, 5, 'Ongerealiseerd koersresultaat valuta', 1, 'ongerealiseerd koersresultaat valuta'),
                                (21, 0, 5, 'Overige vorderingen & overlopende bezittingen', 1, 'Overige vorderingen & overlopende activa'),
                                (22, 0, 6, 'Banktegoeden en kas', 1, 'Liquide middelen'),
                                (23, 0, 7, 'Eigen vermogen begin boekjaar', 1, 'Eigen vermogen begin boekjaar'),
                                (24, 0, 7, 'Saldo opnames en stortingen privé', 1, 'Rekening courant eigenaar'),
                                (25, 0, 7, 'Financiële resultaten lopend boekjaar', 1, 'Financiële resultaten lopend boekjaar'),
                                (26, 0, 8, 'Pensioenvoorzieningen', 1, 'Pensioenvoorzieningen'),
                                (27, 0, 8, 'Reserveringen', 1, 'Reserveringen'),
                                (28, 0, 8, 'Overige voorzieningen', 1, 'Overige voorzieningen'),
                                (29, 0, 9, 'Langlopende leningen', 1, 'Langlopende leningen'),
                                (30, 0, 10, 'Crediteuren (openstaande inkoopfacturen)', 1, 'Crediteuren'),
                                (31, 0, 10, 'Te betalen omzetbelasting', 1, 'Verschuldigde omzetbelasting'),
                                (32, 0, 10, 'Belastingen en premies sociale verzekeringen', 1, 'Belastingen en premies sociale verzekeringen'),
                                (33, 0, 10, 'Schulden aan kredietinstellingen', 1, 'Schulden aan kredietinstellingen'),
                                (34, 0, 10, 'Voorziening vakantiegeld', 1, 'Voorziening vakantiegeld'),
                                (35, 0, 10, 'Overige schulden & overlopende passiva ', 1, 'Overige schulden & overlopende passiva '),
                                (36, 0, 10, 'NOG TE VERWERKEN TRANSACTIES', 1, 'NOG TE VERWERKEN TRANSACTIES'),
                                (37, 0, 11, 'Netto Omzet', 1, 'Netto Omzet'),
                                (38, 0, 12, 'Kostprijs van de omzet', 1, 'Kostprijs van de omzet'),
                                (39, 0, 13, 'Huisvestingskosten', 1, 'Huisvestingskosten'),
                                (40, 0, 13, 'Operationele kosten', 1, 'Exploitatiekosten'),
                                (41, 0, 13, 'Auto- en transportkosten', 1, 'Auto- en transportkosten'),
                                (42, 0, 13, 'Verkoopkosten', 1, 'Verkoopkosten'),
                                (43, 0, 13, 'Kantoorkosten', 1, 'Kantoorkosten'),
                                (44, 0, 13, 'Opleiding en training', 1, 'Opleiding en training'),
                                (45, 0, 13, 'Algemene kosten', 1, 'Algemene kosten'),
                                (46, 0, 13, 'Afschrijvingen', 1, 'Afschrijvingen'),
                                (47, 0, 13, 'Lonen en salarissen', 1, 'Lonen en salarissen'),
                                (48, 0, 13, 'Sociale lasten ', 1, 'Sociale lasten'),
                                (49, 0, 13, 'Pensioenlasten', 1, 'Pensioenlasten'),
                                (50, 0, 13, 'Overige personeelskosten', 1, 'Overige personeelskosten'),
                                (51, 0, 14, 'Betaalde rente en kosten Bank', 1, 'Rentelasten'),
                                (52, 0, 14, 'Ontvangen rente', 1, 'Rentebaten'),
                                (53, 0, 14, 'Incidentele resultaten', 1, 'Incidentele resultaten'),
                                (54, 0, 13, 'Investeringen', 1, 'Investeringen')""";
        db.execute(chartGroupEntry);

        String chartMasterEntry = """INSERT INTO `chart_master` (`id`, `version`, `account_code`, `account_code2`, `account_name`, `chart_group_id`, `ordering`, `status`, `vat_category_id`, `accountant_name`) VALUES
                                (1, 0, '0400', '', 'Goodwill', 1, 1, 1, 1, 'Goodwill'),
                                (2, 0, '0405', '', 'Afschrijving goodwill', 1, 2, 1, 1, 'Afschrijving goodwill'),
                                (3, 0, '0410', '', 'Octrooien, patenten en rechten', 2, 3, 1, 1, 'Octrooien, patenten en rechten'),
                                (4, 0, '0415', '', 'Afschrijving octrooien, patenten en rechten', 2, 4, 1, 1, 'Afschrijving octrooien, patenten en rechten'),
                                (5, 0, '0420', '', 'Research en ontwikkelingskosten', 3, 5, 1, 1, 'Research en ontwikkelingskosten'),
                                (6, 0, '0425', '', 'Afschrijving Research en ontwikkelingskosten', 3, 6, 1, 1, 'Afschrijving Research en ontwikkelingskosten'),
                                (7, 0, '0430', '', 'Overige niet materiële bezittingen', 4, 7, 1, 1, 'Overige immateriële activa'),
                                (8, 0, '0200', '', 'Machines en apparaten', 5, 8, 1, 1, 'Machines en apparaten'),
                                (9, 0, '0201', '', 'Afschrijving machines en apparaten', 5, 9, 1, 1, 'Afschrijving machines en apparaten'),
                                (10, 0, '0210', '', 'Computers en computerapparatuur', 6, 10, 1, 1, 'Computers en computerapparatuur'),
                                (11, 0, '0211', '', 'Afschrijving computers en computerapparatuur', 6, 11, 1, 1, 'Afschrijving computers en computerapparatuur'),
                                (12, 0, '0220', '', 'Auto en transportmiddelen', 7, 12, 1, 1, 'Auto en transportmiddelen'),
                                (13, 0, '0221', '', 'Afschrijving auto en transportmiddelen', 7, 13, 1, 1, 'Afschrijving auto en transportmiddelen'),
                                (14, 0, '0230', '', 'Inventaris - meubelen ed', 8, 14, 1, 1, 'Inventaris - meubelen ed'),
                                (15, 0, '0231', '', 'Afschrijving inventaris', 8, 15, 1, 1, 'Afschrijving inventaris'),
                                (16, 0, '0240', '', 'Gebouwen & grond', 9, 16, 1, 1, 'Gebouwen & grond'),
                                (17, 0, '0241', '', 'Afschrijving gebouwen & grond', 9, 17, 1, 1, 'Afschrijving gebouwen & grond'),
                                (18, 0, '0250', '', 'Overige materiële bezittingen', 10, 18, 1, 1, 'Overige materiële vaste activa'),
                                (19, 0, '0251', '', 'Afschrijving overige materiele bezittingen', 10, 19, 1, 1, 'Afschrijving overige materiële vaste activa'),
                                (20, 0, '0300', '', 'Waarborgsommen', 11, 20, 1, 1, 'Waarborgsommen'),
                                (21, 0, '0310', '', 'Aandeel of belang  in andere bedrijven', 12, 21, 1, 1, 'Deelnemingen in andere bedrijven'),
                                (22, 0, '0320', '', 'Strategische langlopende beleggingen', 13, 22, 1, 1, 'Strategische langlopende beleggingen'),
                                (23, 0, '0330', '', 'Verstrekte langlopende leningen (hypotheek ed)', 14, 23, 1, 1, 'Langlopende leningen'),
                                (24, 0, '0340', '', 'Overige financiële bezittingen', 15, 24, 1, 1, 'Overige financiële vaste activa'),
                                (25, 0, '3000', '', 'Voorraad', 16, 25, 1, 1, 'Voorraad'),
                                (26, 0, '1300', '', 'Debiteuren (openstaande verkoopfacturen)', 17, 26, 1, 1, 'Handelsdebiteuren'),
                                (27, 0, '1301', '', 'Debiteuren openingsbalans', 17, 27, 1, 1, 'Debiteuren openingsbalans'),
                                (28, 0, '1450', '', 'Beleggingen', 19, 28, 1, 1, 'Beleggingen'),
                                (29, 0, '1460', '', 'Ongerealiseerd koersresultaat valuta', 20, 29, 1, 1, 'Ongerealiseerd koersresultaat valuta'),
                                (30, 0, '1585', '', 'Te ontvangen Belasting en premies sociale verzekeringen', 18, 30, 1, 1, 'Belasting en premies sociale verzekeringen'),
                                (31, 0, '1350', '', 'Overige vorderingen', 21, 31, 1, 1, 'Overige vorderingen'),
                                (32, 0, '1510', '', 'Nog te factureren omzet', 21, 32, 1, 1, 'Nog te factureren omzet'),
                                (33, 0, '1520', '', 'Vooruitbetaalde kosten', 21, 33, 1, 1, 'Vooruitbetaalde kosten'),
                                (34, 0, '1530', '', 'Nog te ontvangen rente', 21, 34, 1, 1, 'Nog te ontvangen rente'),
                                (35, 0, '1540', '', 'Overige vorderingen door afsluiting boekjaar', 21, 35, 1, 1, 'Overige overlopende activa'),
                                (36, 0, '1100', '', 'Bank', 22, 36, 1, 1, 'Bank'),
                                (37, 0, '1101', '', 'Bank2', 22, 37, 1, 1, 'Bank2'),
                                (38, 0, '1102', '', 'Bank3', 22, 38, 1, 1, 'Bank3'),
                                (39, 0, '1103', '', 'Bank4', 22, 39, 1, 1, 'Bank4'),
                                (40, 0, '1104', '', 'Bank5', 22, 40, 1, 1, 'Bank5'),
                                (41, 0, '1105', '', 'Bank6', 22, 41, 1, 1, 'Bank6'),
                                (42, 0, '1106', '', 'Bank7', 22, 42, 1, 1, 'Bank7'),
                                (43, 0, '1107', '', 'Bank8', 22, 43, 1, 1, 'Bank8'),
                                (44, 0, '1108', '', 'Bank9', 22, 44, 1, 1, 'Bank9'),
                                (45, 0, '1109', '', 'Bank10', 22, 45, 1, 1, 'Bank10'),
                                (46, 0, '1110', '', 'Bank11', 22, 46, 1, 1, 'Bank11'),
                                (47, 0, '1200', '', 'Kas', 22, 47, 1, 1, 'Kas'),
                                (48, 0, '0100', '', 'Totaal vermogen van het bedrijf', 23, 48, 1, 1, 'Eigen vermogen'),
                                (49, 0, '1400', '', 'Rekening courant eigenaar - Privé stortingen', 24, 49, 1, 1, 'Rekening courant eigenaar'),
                                (50, 0, '1410', '', 'Privé opnames', 24, 50, 1, 1, 'Opnames privé'),
                                (51, 0, '0120', '', 'Resultaat lopend boekjaar', 25, 51, 1, 1, 'Resultaat lopend boekjaar'),
                                (52, 0, '0700', '', 'Pensioenvoorzieningen', 26, 52, 1, 1, 'Pensioenvoorzieningen'),
                                (53, 0, '0110', '', 'Reserveringen', 27, 53, 1, 1, 'Reserveringen'),
                                (54, 0, '0710', '', 'Overige voorzieningen', 28, 54, 1, 1, 'Overige voorzieningen'),
                                (55, 0, '0600', '', 'Langlopende leningen (ontvangen)', 29, 55, 1, 1, 'Langlopende leningen'),
                                (56, 0, '1600', '', 'Crediteuren (openstaande inkoopfacturen)', 30, 56, 1, 1, 'Crediteuren'),
                                (57, 0, '1601', '', 'Crediteuren openingsbalans', 30, 57, 1, 1, 'Crediteuren openingsbalans'),
                                (58, 0, '1650', '', 'Kortlopende leningen en schulden', 33, 58, 1, 1, 'Kortlopende leningen'),
                                (59, 0, '2105', '', 'Terug te vragen BTW (hoog)', 31, 59, 1, 1, 'Te vorderen BTW (hoog)'),
                                (60, 0, '2115', '', 'Terug te vragen BTW (laag)', 31, 60, 1, 1, 'Te vorderen BTW (laag)'),
                                (61, 0, '2100', '', 'Te betalen BTW (hoog)', 31, 61, 1, 1, 'Te betalen BTW (hoog)'),
                                (62, 0, '2110', '', 'Te betalen BTW (laag)', 31, 62, 1, 1, 'Te betalen BTW (laag)'),
                                (63, 0, '1550', '', 'Te betalen of te ontvangen omzetbelasting', 31, 63, 1, 1, 'Te betalen of te ontvangen omzetbelasting'),
                                (64, 0, '1560', '', 'Nog te betalen loonbelasting', 32, 64, 1, 1, 'Nog te betalen loonbelasting'),
                                (65, 0, '1570', '', 'Nog te betalen overige belastingen en premies sociale verzekeringen', 32, 65, 1, 1, 'Nog te betalen overige belastingen en premies sociale verzekeringen'),
                                (66, 0, '1595', '', 'Nog te betalen vakantiegeld', 34, 66, 1, 1, 'Voorziening vakantiegeld'),
                                (67, 0, '2220', '', 'Leningen en afbetalingen', 35, 67, 1, 1, 'Leningen en afbetalingen'),
                                (68, 0, '1580', '', 'Nog te betalen posten - factuur nog niet gehad', 35, 68, 1, 1, 'Overlopende passiva - vooruitbetaalde facturen'),
                                (69, 0, '1590', '', 'Reservering accountant en boekhouder', 35, 69, 1, 1, 'Reservering accountant en boekhouder'),
                                (70, 0, '1655', '', 'Overige schulden zakelijk', 35, 70, 1, 1, 'Overige schulden zakelijk'),
                                (71, 0, '2200', '', 'Kruisposten', 36, 71, 1, 1, 'Kruisposten'),
                                (72, 0, '2210', '', 'Tussenrekening interne betalingen', 36, 71, 1, 1, 'Tussenrekening interne betalingen'),
                                (73, 0, '2300', '', 'Niet verwerkte banktransacties', 36, 72, 1, 1, 'Ongerubriceerde banktransacties'),
                                (74, 0, '8000', '', 'Verkoop geleverde diensten (BTW hoog)', 37, 73, 1, 1, 'Omzet geleverde diensten BTW hoog'),
                                (75, 0, '8100', '', 'Verkoop goederen (BTW hoog)', 37, 74, 1, 1, 'Omzet goederen BTW hoog'),
                                (76, 0, '8200', '', 'Verkoop (BTW hoog)', 37, 75, 1, 1, 'Omzet overig BTW hoog'),
                                (77, 0, '8800', '', 'Verkoop (BTW laag)', 37, 76, 1, 1, 'Omzet BTW laag'),
                                (78, 0, '8900', '', 'Verkoop (BTW nul)', 37, 77, 1, 1, 'Omzet BTW nul'),
                                (79, 0, '8950', '', 'Verkoop buitenland binnen EU', 37, 78, 1, 1, 'Omzet buitenland binnen EU'),
                                (80, 0, '8990', '', 'Verkoop buitenland buiten EU', 37, 79, 1, 1, 'Omzet buitenland buiten EU'),
                                (81, 0, '7000', '', 'Kostprijs - inkoop materialen tbv omzet', 38, 80, 1, 1, 'Kostprijs - inkoop materialen tbv omzet'),
                                (82, 0, '7100', '', 'Kostprijs - Inhuur van derden', 38, 81, 1, 1, 'Kostprijs - Inhuur van derden'),
                                (83, 0, '7200', '', 'Kostprijs - inkoop overig tbv omzet', 38, 82, 1, 1, 'Kostprijs - inkoop overig tbv omzet'),
                                (84, 0, '4400', '', 'Huur bedrijfsruimte', 39, 83, 1, 1, 'Huur bedrijfsruimte'),
                                (85, 0, '4405', '', 'Servicekosten & schoonmaak', 39, 84, 1, 1, 'Servicekosten & schoonmaak'),
                                (86, 0, '4410', '', 'Electra en gas', 39, 85, 1, 1, 'Electra en gas'),
                                (87, 0, '4415', '', 'Onderhoud en reparaties', 39, 86, 1, 1, 'Onderhoud en reparaties'),
                                (88, 0, '4425', '', 'Overige huisvestingskosten', 39, 87, 1, 1, 'Overige huisvestingskosten'),
                                (89, 0, '4200', '', 'Projecten & ontwikkelingskosten', 40, 88, 1, 1, 'Projecten & ontwikkelingskosten'),
                                (90, 0, '4220', '', 'Exploitatiekosten', 40, 89, 1, 1, 'Exploitatiekosten'),
                                (91, 0, '4210', '', 'Exploitatie - reparatie en onderhoud', 40, 90, 1, 1, 'Exploitatie - reparatie en onderhoud'),
                                (92, 0, '4700', '', 'Autokosten', 41, 91, 1, 1, 'Autokosten'),
                                (93, 0, '4705', '', 'Brandstofkosten', 41, 92, 1, 1, 'Brandstofkosten'),
                                (94, 0, '4706', '', 'Reparatie en onderhoud', 41, 93, 1, 1, 'Reparatie en onderhoud'),
                                (95, 0, '4707', '', 'Parkeerkosten', 41, 94, 1, 1, 'Parkeerkosten'),
                                (96, 0, '4708', '', 'Autokosten BTW correctie privégebruik', 41, 95, 1, 1, 'Autokosten BTW correctie privégebruik'),
                                (97, 0, '4760', '', 'Kilometervergoeding aan privé', 41, 96, 1, 1, 'Kilometervergoeding aan privé'),
                                (98, 0, '4710', '', 'Reiskosten OV*', 41, 97, 1, 1, 'Reiskosten OV'),
                                (99, 0, '4740', '', 'Overige Reiskosten', 41, 98, 1, 1, 'Overige Reiskosten'),
                                (100, 0, '4600', '', 'Marketing, reclame & verkoop kosten', 42, 99, 1, 1, 'Marketing, reclame & verkoop kosten'),
                                (101, 0, '4605', '', 'Hosting, website & domeinnamen', 42, 100, 1, 1, 'Hosting, website & domeinnamen'),
                                (102, 0, '4610', '', 'Drukwerk inc. Visitekaartjes, briefpapier, folders ed.', 42, 101, 1, 1, 'Handelsdrukwerk'),
                                (103, 0, '4100', '', 'Eten, drinken, boodschappen en kado''s*', 42, 102, 1, 1, 'Representatiekosten'),
                                (104, 0, '4270', '', 'Reis -en verblijfkosten*', 42, 103, 1, 1, 'Reis en verblijfskosten'),
                                (105, 0, '4630', '', 'Sponsoring', 42, 104, 1, 1, 'Sponsoring'),
                                (106, 0, '4650', '', 'Afboeken niet betaalde verkoopfacturen', 42, 105, 1, 1, 'Afschrijving dubieuze debiteuren'),
                                (107, 0, '4430', '', 'Kantoorbenodigdheden', 43, 106, 1, 1, 'Kantoorbenodigdheden'),
                                (108, 0, '4450', '', 'Abonnementen en contributies', 43, 107, 1, 1, 'Abonnementen en contributies'),
                                (109, 0, '4500', '', 'Werkplek en gereedschap kosten', 43, 108, 1, 1, 'Werkplek en gereedschap kosten'),
                                (110, 0, '4520', '', 'Telefoon, fax en internet', 43, 109, 1, 1, 'Telefoon & internet'),
                                (111, 0, '4540', '', 'Porti en Vracht', 43, 110, 1, 1, 'Porti en Vracht'),
                                (112, 0, '4440', '', 'Vakliteratuur en documentatie', 43, 111, 1, 1, 'Vakliteratuur en documentatie'),
                                (113, 0, '4435', '', 'Kantoorkosten - privégebruik', 43, 112, 1, 1, 'Kantoorkosten - privégebruik'),
                                (114, 0, '4110', '', 'Congressen, seminars en studiereizen*', 44, 113, 1, 1, 'Congressen, seminars en studiereizen'),
                                (115, 0, '4150', '', 'Workshops, training en opleiding*', 44, 114, 1, 1, 'Workshops, training en opleiding'),
                                (116, 0, '4160', '', 'Studieboeken & materiaal*', 44, 115, 1, 1, 'studieboeken & materiaal'),
                                (117, 0, '4170', '', 'Overige opleiding en scholing*', 44, 116, 1, 1, 'Overige opleiding en scholing'),
                                (118, 0, '4800', '', 'administratie en accountantskosten', 45, 117, 1, 1, 'Kosten administrateur & accountant'),
                                (119, 0, '4820', '', 'Deurwaarders en Incassobureaus', 45, 118, 1, 1, 'Deurwaarders en Incassobureaus'),
                                (120, 0, '4840', '', 'Adviseurs, Juristen en overige advieskosten', 45, 119, 1, 1, 'Adviseurs, Juristen en overige advieskosten'),
                                (121, 0, '4120', '', 'Kantinekosten - voedsel en dranken', 45, 120, 1, 1, 'Overige representatiekosten*'),
                                (122, 0, '4890', '', 'Kleine aanschaffingen', 45, 121, 1, 1, 'Kleine aanschaffingen'),
                                (123, 0, '4455', '', 'Overige verzekeringen', 45, 122, 1, 1, 'Overige verzekeringen'),
                                (124, 0, '4895', '', 'Overige bedrijfskosten', 45, 123, 1, 1, 'Overige bedrijfskosten'),
                                (125, 0, '4905', '', 'Afschrijving machines en apparaten', 46, 124, 1, 1, 'Afschrijving machines en apparaten'),
                                (126, 0, '4910', '', 'Afschrijving computers en computerapparatuur', 46, 125, 1, 1, 'Afschrijving computers en computerapparatuur'),
                                (127, 0, '4915', '', 'Afschrijving auto en transportmiddelen', 46, 126, 1, 1, 'Afschrijving auto en transportmiddelen'),
                                (128, 0, '4920', '', 'Afschrijving inventaris', 46, 127, 1, 1, 'Afschrijving inventaris'),
                                (129, 0, '4925', '', 'Afschrijving gebouwen & grond', 46, 128, 1, 1, 'Afschrijving gebouwen & grond'),
                                (130, 0, '4930', '', 'Afschrijving overige materiële bezittingen', 46, 129, 1, 1, 'Afschrijving overige materiele vaste activa'),
                                (131, 0, '4900', '', 'Afschrijvingen Overige', 46, 130, 1, 1, 'Afschrijvingen'),
                                (132, 0, '4300', '', 'lonen en salarissen', 47, 131, 1, 1, 'lonen en salarissen'),
                                (133, 0, '4310', '', 'Loonheffing', 47, 132, 1, 1, 'Loonheffing'),
                                (134, 0, '4320', '', 'Mutatie Vakantiegeld', 47, 133, 1, 1, 'Mutatie Vakantiegeld'),
                                (135, 0, '4325', '', 'Zorgverzekeringswet', 48, 134, 1, 1, 'Zorgverzekeringswet'),
                                (136, 0, '4326', '', 'Sociale lasten', 48, 135, 1, 1, 'Sociale lasten'),
                                (137, 0, '4340', '', 'Pensioenpremies medewerkers', 49, 136, 1, 1, 'Pensioenpremies medewerkers'),
                                (138, 0, '4000', '', 'Lijfrente premies*', 49, 137, 1, 1, 'Lijfrente premies'),
                                (139, 0, '4005', '', 'Dotatie voorziening pensioen in eigen beheer*', 49, 138, 1, 1, 'Dotatie voorziening pensioen in eigen beheer'),
                                (140, 0, '4020', '', 'Reservering fiscale oudedags reserve (FOR)*', 49, 139, 1, 1, 'Reservering fiscale oudedags reserve (FOR)'),
                                (141, 0, '4010', '', 'Arbeidsongeschiktheid verzekering (AOV)*', 50, 140, 1, 1, 'Arbeidsongeschiktheid verzekering (AOV)'),
                                (142, 0, '4030', '', 'Overige inkomsten verzekeringen', 50, 141, 1, 1, 'Overige inkomsten verzekeringen'),
                                (143, 0, '4040', '', 'Ingehuurde Krachten', 50, 142, 1, 1, 'Inleen en uitzendkrachten'),
                                (144, 0, '4050', '', 'Personeels verzekeringen', 50, 143, 1, 1, 'Personeels verzekeringen'),
                                (145, 0, '4330', '', 'Reiskostenvergoeding personeel', 50, 144, 1, 1, 'reiskostenvergoeding medewerkers'),
                                (146, 0, '4350', '', 'Overige Personeelskosten - loondienstmedewerkers', 50, 145, 1, 1, 'overige Personeelskosten'),
                                (147, 0, '4360', '', 'Studie en opleidingskosten personeel', 50, 146, 1, 1, 'Studie en opleidingskosten'),
                                (148, 0, '4370', '', 'Wervingskosten personeel', 50, 147, 1, 1, 'Wervingskosten personeel'),
                                (149, 0, '4960', '', 'Betaalde rente en kosten bank', 51, 148, 1, 1, 'Betaalde rente en kosten bank'),
                                (150, 0, '4970', '', 'Betaalde rente op openstaande vorderingen', 51, 149, 1, 1, 'Betaalde rente op openstaande vorderingen'),
                                (151, 0, '4980', '', 'Betaalde rente op rekening courant eigenaar', 51, 150, 1, 1, 'Betaalde rente op rekening courant eigenaar'),
                                (152, 0, '4985', '', 'Betaalde rente Overige', 51, 151, 1, 1, 'Betaalde rente'),
                                (153, 0, '4990', '', 'Ontvangen rente verstrekte leningen', 52, 152, 1, 1, 'Ontvangen rente verstrekte leningen'),
                                (154, 0, '4995', '', 'Ontvangen rente overige', 52, 153, 1, 1, 'Ontvangen rente'),
                                (155, 0, '9900', '', 'Buitengewone baten', 53, 154, 1, 1, 'Buitengewone baten'),
                                (157, 0, '9910', '', 'Overige incidentele resultaten', 53, 156, 1, 1, 'Overige incidentele resultaten'),
                                (158, 0, '9930', '', 'Opbrengsten kleine ondernemersregeling BTW', 53, 157, 1, 1, 'Opbrengsten kleine ondernemersregeling BTW'),
                                (159, 0, '4999', '4999', 'Software Ontwikkeling', 40, 1, 1, 1, 'Software'),
                                (160, 0, '1111', '', 'ABN-AMRO(LM Beheer)', 22, 1, 1, 1, 'ABN-AMRO(LM Beheer)'),
                                (161, 0, '4001', '', 'Investeringen lopend boekjaar', 46, 124, 1, 1, 'Investeringen lopend boekjaar'),
                                (162, 0, '1112', '', '197638279(197638279)', 22, 1, 1, 1, '197638279(197638279)'),
                                (163, 0, '1113', '', '486335(486335)', 22, 1, 1, 1, '486335(486335)'),
                                (164, 0, '1114', '', 'Triodos spaarrekening', 22, 1, 1, 1, 'Triodos spaarrekening'),
                                (165, 0, '1115', '', 'gfsdgbfdb', 22, 1, 1, 1, 'gfsdgbfdb'),
                                (166, 0, '1116', '', '4316016', 22, 1, 1, 1, '4316016'),
                                (167, 0, '1117', '', '335984738', 22, 1, 1, 1, '335984738'),
                                (168, 0, '1118', '', '177815671', 22, 1, 1, 1, '177815671'),
                                (169, 0, '1119', '', '554073390', 22, 1, 1, 1, '554073390'),
                                (170, 0, '1120', '', 'La Montagne Beheer BV \t', 22, 1, 1, 1, 'La Montagne Beheer BV \t'),
                                (171, 0, '1121', '', 'Trepol Utrech', 22, 1, 1, 1, 'Trepol Utrech'),
                                (172, 0, '1122', '', 'pensioenrekening', 22, 1, 1, 1, 'pensioenrekening'),
                                (173, 0, '1123', '', 'INGB', 22, 1, 1, 1, 'INGB'),
                                (174, 0, '1124', '', 'ASN 707486335', 22, 1, 1, 1, 'ASN 707486335'),
                                (175, 0, '1125', '', '167869868', 22, 1, 1, 1, '167869868'),
                                (176, 0, '1126', '', '394591038', 22, 1, 1, 1, '394591038'),
                                (177, 0, '1127', '', 'test', 22, 1, 1, 1, 'test'),
                                (178, 0, '1128', '', 'p', 22, 1, 1, 1, 'p'),
                                (179, 0, '1129', '', 'Brand Inc', 22, 1, 1, 1, 'Brand Inc'),
                                (180, 0, '1130', '', 'Aircraft Corporation', 22, 1, 1, 1, 'Aircraft Corporation'),
                                (181, 0, '1131', '', 'Dravid', 22, 1, 1, 1, 'Dravid')""";
        db.execute(chartMasterEntry);

    }

    def destroy = {
    }
}
