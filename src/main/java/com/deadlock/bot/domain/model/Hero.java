package com.deadlock.bot.domain.model;

/**
 * Справочник героев Deadlock.
 * Сопоставляет ID из API с реальными именами персонажей.
 */
public enum Hero {
    INFERNUS(1, "Infernus"),
    SEVEN(2, "Seven"),
    VINDICTA(3, "Vindicta"),
    LADY_GEIST(4, "Lady Geist"),
    ABRAMS(6, "Abrams"),
    WRAITH(7, "Wraith"),
    MCGINNIS(8, "McGinnis"),
    PARADOX(10, "Paradox"),
    DYNAMO(11, "Dynamo"),
    KELVIN(12, "Kelvin"),
    HAZE(13, "Haze"),
    HOLLIDAY(14, "Holliday"),
    BEBOP(15, "Bebop"),
    CALICO(16, "Calico"),
    GREY_TALON(17, "Grey Talon"),
    MO_AND_KRILL(18, "Mo & Krill"),
    SHIV(19, "Shiv"),
    IVY(20, "Ivy"),
    KALI(21, "Kali"),
    WARDEN(25, "Warden"),
    YAMATO(27, "Yamato"),
    LASH(31, "Lash"),
    VISCOUS(35, "Viscous"),
    GUNSLINGER(38, "Gunslinger"),
    THE_BOSS(39, "The Boss"),
    GENERIC_PERSON(46, "Generic Person"),
    TOKAMAK(47, "Tokamak"),
    WRECKER(48, "Wrecker"),
    RUTGER(49, "Rutger"),
    POCKET(50, "Pocket"),
    THUMPER(51, "Thumper"),
    MIRAGE(52, "Mirage"),
    FATHOM(53, "Fathom"),
    CADENCE(54, "Cadence"),
    TARGET_DUMMY(55, "TargetDummy"),
    BOMBER(56, "Bomber"),
    SHIELD_GUY(57, "Shield Guy"),
    VYPER(58, "Vyper"),
    VANDAL(59, "Vandal"),
    SINCLAIR(60, "Sinclair"),
    TRAPPER(61, "Trapper"),
    RAVEN(62, "Raven"),
    MINA(63, "Mina"),
    DRIFTER(64, "Drifter"),
    VENATOR(65, "Venator"),
    VICTOR(66, "Victor"),
    PAIGE(67, "Paige"),
    THE_DOORMAN(69, "The Doorman"),
    BILLY(72, "Billy"),
    DRUID(73, "Druid"),
    GRAF(74, "Graf"),
    FORTUNA(75, "Fortuna"),
    GRAVES(76, "Graves"),
    APOLLO(77, "Apollo"),
    AIRHEART(78, "Airheart"),
    REM(79, "Rem"),
    SILVER(80, "Silver"),
    CELESTE(81, "Celeste"),
    OPERA(82, "Opera"),
    UNKNOWN(0, "Неизвестный герой"); // Заглушка на случай ошибок

    private final int id;
    private final String name;

    Hero(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Удобный метод для быстрого поиска имени по ID.
     * Если Valve добавит нового героя, а мы забыли его сюда вписать,
     * бот не упадет, а просто выведет "Неизвестный герой (ID)".
     */
    public static String getNameById(int id) {
        for (Hero hero : values()) {
            if (hero.getId() == id) {
                return hero.getName();
            }
        }
        return "Неизвестный герой (" + id + ")";
    }
}