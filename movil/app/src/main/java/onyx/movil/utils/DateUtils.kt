package onyx.movil.utils

// 2026-01-31T23:32:01.16277
fun formatearFecha(fecha: String): String {

    val fechaHora = fecha.split("T")

    val yyyyMmDd = fechaHora[0].split("-")

    val yyyy = yyyyMmDd[0]
    val mm = yyyyMmDd[1]
    val dd = yyyyMmDd[2]

    return "$dd/$mm/$yyyy"
}

fun formatearFechaHora(fecha: String): String {

    val fechaHora = fecha.split("T")
    val horaSegundos = fechaHora[1].split(":")

    val fechaFormateada = formatearFecha(fecha)
    val hora = horaSegundos[0] + ":" + horaSegundos[1]

    return "$fechaFormateada $hora"
}
