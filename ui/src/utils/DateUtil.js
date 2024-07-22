
export default {

    dateFormat(dateStr) {
        try {
            const date = new Date(dateStr);
            const formatter = new Intl.DateTimeFormat('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });

            return formatter.format(date);
        } catch (e) {
            return dateStr;
        }
    }
};
